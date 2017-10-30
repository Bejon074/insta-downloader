package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.helper.Constants;
import lombok.extern.slf4j.Slf4j;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private Instagram instagram;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ProfileService profileService;

    @Scheduled(fixedRate = Constants.poolingTimeGap)
    public void TryToGetNextMedias() throws IOException {

        List<UserDetail> userDetails = profileService.getProfiles();
        for (UserDetail userDetail : userDetails) {
            log.info("scheduler starts for user: {}", userDetail.getUserName());
            Account account = instagram.getAccountByUsername(userDetail.getUserName());
            downloadService.startDownload(account, userDetail.getFileSavingDirectory(), userDetail);
        }
    }
}
