package com.qminder.instadownloader.service;

import com.qminder.instadownloader.Enum.MediaType;
import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.helper.Constants;
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
    private ProfileService profileService;

    @Async("threadPoolTaskExecutor")
    @Override
    public void startDownload(Account account, String directory, UserDetail savedUserDetail) {
        String maxId = savedUserDetail == null ? "" : savedUserDetail.getLastDownloadedFileId();
        Path path = pathResolverService.getPath(directory, account.username);
        try {
            for (int i = 0; i < account.mediaCount / Constants.downloadChunkSize + 1; i++) {
                List<Media> medias = instagram.getMedias(account.username, Constants.downloadChunkSize, maxId);
                int imageCounter = 0;
                for (Media media : medias) {
                    if (media.type.equals(MediaType.IMAGE.getValue())) {
                        try (InputStream in = new URL(media.imageUrls.high).openStream()) {
                            Files.copy(in, Paths.get(path + "\\" + media.imageUrls.high
                                    .substring(media.imageUrls.high.lastIndexOf('/'))));
                        } catch (FileAlreadyExistsException ex) {
                            log.info("file already exits with id: {}", media.id);
                            imageCounter--;
                        }
                        imageCounter++;
                    }
                }
                maxId = medias.size() == 0 ? maxId : medias.get(medias.size() - 1).id;
                savedUserDetail = saveOrUpdateUserDetail(savedUserDetail, account, maxId, imageCounter, directory);
            }
        } catch (Exception ex) {
            if (!maxId.isEmpty() && account.username != null) {
                saveOrUpdateUserDetail(savedUserDetail, account, maxId, 0, directory);
            }
            log.error("error {}", ex);
        }
    }

    private UserDetail saveOrUpdateUserDetail(UserDetail savedUserDetail,
                                              Account account,
                                              String maxId,
                                              int imageCounter,
                                              String fileSavingDirectory) {
        UserDetail userDetail = savedUserDetail == null ? new UserDetail() : savedUserDetail;
        userDetail.setTotalFileDownloaded(userDetail.getTotalFileDownloaded() + imageCounter);
        userDetail.setFullName(account.fullName);
        userDetail.setLastDownloadedFileId(maxId);
        userDetail.setUserName(account.username);
        userDetail.setFileSavingDirectory(fileSavingDirectory);
        log.info("saving userDetail: {}", userDetail);
        return profileService.saveUserDetail(userDetail);
    }
}
