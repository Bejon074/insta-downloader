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

    @Override
    public void startNewDownload(String userName, String directory) {
        UserDetail savedUserDetail = userRepository.findUserDetailByUserName(userName);
        String maxId = savedUserDetail == null ? "" : savedUserDetail.getLastDownloadedFileId();
        log.info("maxId, {}", maxId );
        Path path = pathResolverService.getPath(directory, userName);
        startDownload(userName, maxId, path, directory, savedUserDetail);
    }

    private void saveOrUpdateUserDetail(UserDetail savedUserDetail,
                                        Account account,
                                        String maxId,
                                        int totalCounter,
                                        String fileSavingDirectory){
        UserDetail userDetail = savedUserDetail == null ? new UserDetail() : savedUserDetail;
        userDetail.setTotalFileDownloaded(userDetail.getTotalFileDownloaded() + totalCounter);
        userDetail.setFullName(account.fullName);
        userDetail.setLastDownloadedFileId(maxId);
        userDetail.setUserName(account.username);
        userDetail.setFileSavingDirectory(fileSavingDirectory);
        userRepository.saveAndFlush(userDetail);
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void startDownload(String userName,
                              String maxId,
                              Path path,
                              String directory,
                              UserDetail savedUserDetail) {
        Account account = new Account();
        int totalCounter = 0;
        try {
            account = instagram.getAccountByUsername(userName);
            for (int i = 0; i < account.mediaCount / Constants.downloadChunkSize + 1; i++) {
                List<Media> medias = instagram.getMedias(userName, Constants.downloadChunkSize, maxId);
                for (Media media : medias) {
                    if (media.type.equals(MediaType.IMAGE.getValue())) {
                        try (InputStream in = new URL(media.imageUrls.high).openStream()) {
                            Files.copy(in, Paths.get(path + "\\" + media.imageUrls.high
                                    .substring(media.imageUrls.high.lastIndexOf('/'))));
                        } catch (FileAlreadyExistsException ex) {
                            log.info("file already exits with id: {}", media.id);
                            totalCounter--;
                        }
                        totalCounter++;
                    }
                }
                maxId = medias.size() == 0 ? maxId : medias.get(medias.size() - 1).id;
            }
            saveOrUpdateUserDetail(savedUserDetail, account, maxId, totalCounter, directory);
        } catch (Exception ex) {
            if (!maxId.isEmpty() && account.username != null) {
                saveOrUpdateUserDetail(savedUserDetail, account, maxId, totalCounter, directory);
            }
            log.error("error {}", ex);
        }
    }
}
