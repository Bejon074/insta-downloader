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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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
    public void startDownload(Account account, String directory, UserDetail savedUserDetail, boolean isNewDownload) {
        String maxId = savedUserDetail == null ? "" : savedUserDetail.getLastDownloadedFileId();
        Path path = pathResolverService.getPath(directory, account.username);
        try {
            if (!isNewDownload) {
                processSchedulerDownload(savedUserDetail, path, maxId, account);
            }
            Media latestMediaDownloaded = new Media();
            for (int i = 0; i < account.mediaCount / Constants.downloadChunkSize + 1; i++) {
                List<Media> medias = instagram.getMedias(account.username, Constants.downloadChunkSize, maxId);
                int imageCounter = 0;
                for (Media media : medias) {
                    if (media.type.equals(MediaType.IMAGE.getValue())) {
                        try (InputStream in = new URL(media.imageUrls.high).openStream()) {
                            if (latestMediaDownloaded.createdTime < media.createdTime) {
                                latestMediaDownloaded = media;
                            }
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
                savedUserDetail = saveOrUpdateUserDetail(savedUserDetail, account, maxId, imageCounter, directory, new Date(latestMediaDownloaded.createdTime));
            }
        } catch (Exception ex) {
            if (!maxId.isEmpty() && account.username != null) {
                saveOrUpdateUserDetail(savedUserDetail, account, maxId, 0, directory, new Date());
            }
            log.error("error {}", ex);
        }
    }

    private UserDetail saveOrUpdateUserDetail(UserDetail savedUserDetail,
                                              Account account,
                                              String maxId,
                                              int imageCounter,
                                              String fileSavingDirectory,
                                              Date uploadTime) {
        UserDetail userDetail = savedUserDetail == null ? new UserDetail() : savedUserDetail;
        userDetail.setTotalFileDownloaded(userDetail.getTotalFileDownloaded() + imageCounter);
        userDetail.setFullName(account.fullName);
        userDetail.setLastDownloadedFileId(maxId);
        userDetail.setUserName(account.username);
        userDetail.setFileSavingDirectory(fileSavingDirectory);
        userDetail.setUploadTime(uploadTime);
        log.info("saving userDetail: {}", userDetail);
        return profileService.saveUserDetail(userDetail);
    }

    private void processSchedulerDownload(UserDetail userDetail, Path path, String maxId, Account account) throws IOException {
        List<Media> medias = instagram.getMedias(userDetail.getUserName(), Constants.downloadChunkSize);
        int imageCounter = 0;
        Media latestMediaDownloaded = new Media();
        for (Media media : medias) {
            if (media.type.equals(MediaType.IMAGE.getValue())) {
                try (InputStream in = new URL(media.imageUrls.high).openStream()) {
                    if (latestMediaDownloaded.createdTime < media.createdTime && userDetail.getUploadTime().getTime() < media.createdTime) {
                        latestMediaDownloaded = media;
                        Files.copy(in, Paths.get(path + "\\" + media.imageUrls.high
                                .substring(media.imageUrls.high.lastIndexOf('/'))));
                    }
                } catch (FileAlreadyExistsException ex) {
                    log.info("file already exits with id: {}", media.id);
                    imageCounter--;
                }
                imageCounter++;
            }
        }
        maxId = medias.size() == 0 ? maxId : medias.get(medias.size() - 1).id;
        if (latestMediaDownloaded.createdTime != 0)
            saveOrUpdateUserDetail(userDetail, account, maxId, imageCounter, userDetail.getFileSavingDirectory(), new Date(latestMediaDownloaded.createdTime));
    }
}
