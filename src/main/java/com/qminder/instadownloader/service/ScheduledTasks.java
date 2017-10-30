package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.repository.UserRepository;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.domain.Account;
import me.postaddict.instagram.scraper.domain.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private Instagram instagram;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PathResolverService pathResolverService;

    @Autowired
    private DownloadService downloadService;

    @Scheduled(fixedRate = 5*60*1000)
    public void TryToGetNextMedias() throws IOException {

        List<UserDetail> userDetails = userRepository.findAll();

        for(UserDetail userDetail: userDetails){
            Path path = pathResolverService.getPath(userDetail.getFileSavingDirectory(), userDetail.getUserName());
            downloadService.startDownload(userDetail.getUserName(), userDetail.getLastDownloadedFileId(),
                    path, userDetail.getFileSavingDirectory(), userDetail);
        }
    }
}
