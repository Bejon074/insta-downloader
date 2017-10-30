package com.qminder.instadownloader.service;

import com.qminder.instadownloader.Enum.MediaType;
import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.helper.Constants;
import com.qminder.instadownloader.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.domain.Account;
import me.postaddict.instagram.scraper.domain.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private PathResolverService pathResolverService;

    @Autowired
    private Instagram instagram;

    @Autowired
    private UserRepository userRepository;

    @Async
    @Override
    public void startNewDownload(String userName, String directory) {
        UserDetail savedUserDetail = userRepository.findUserDetailByUserName(userName);
        String maxID = savedUserDetail == null ? "" : savedUserDetail.getLastDownloadedFileId();
        log.info("maxID, {}", maxID );
        Path path = pathResolverService.getPath(directory, userName);
        Account account = new Account();
        int totalCounter = 0;
        try {
            account = instagram.getAccountByUsername(userName);
            for (int i = 0; i < account.mediaCount / Constants.downloadChunkSize + 1; i++) {
                List<Media> medias = instagram.getMedias(userName, Constants.downloadChunkSize, maxID);
                for (Media media : medias) {
                    if (media.type.equals(MediaType.IMAGE.getValue())) {
                        try (InputStream in = new URL(media.imageUrls.high).openStream()) {
                            Files.copy(in, Paths.get(path + "\\" + media.imageUrls.high.substring(media.imageUrls.high.lastIndexOf('/'))));
                        } catch (FileAlreadyExistsException ex) {
                            log.info("file already exits with id: {}", media.id);
                            totalCounter--;
                        }
                        totalCounter++;
                    }
                }
                maxID = medias.size() == 0 ? maxID : medias.get(medias.size() - 1).id;
            }
            saveOrUpdateUserDetail(savedUserDetail, account, maxID, totalCounter);
        } catch (Exception ex) {
            if (!maxID.isEmpty() && account.username != null) {
                saveOrUpdateUserDetail(savedUserDetail, account, maxID, totalCounter);
            }
            log.error("error {}", ex);
        }
    }

    private void saveOrUpdateUserDetail(UserDetail savedUserDetail, Account account, String maxId, int totalCounter){
        UserDetail userDetail = savedUserDetail == null ? new UserDetail() : savedUserDetail;
        userDetail.setTotalFileDownloaded(userDetail.getTotalFileDownloaded() + totalCounter);
        userDetail.setFullName(account.fullName);
        userDetail.setLastDownloadedFileId(maxId);
        userDetail.setUserName(account.username);
        userRepository.saveAndFlush(userDetail);
    }
}
