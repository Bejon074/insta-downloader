package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.RealTimeUserDetail;
import com.qminder.instadownloader.helper.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ProfileService profileService;

    @Scheduled(fixedRate = Constants.poolingTimeGap)
    public void TryToGetNextMedias() throws IOException {

        List<RealTimeUserDetail> userDetails = profileService.getProfiles();
        for (RealTimeUserDetail userDetail : userDetails) {
            log.info("scheduler starts for user: {}", userDetail.getUserName());
            downloadService.handleScheduleDownload(userDetail);
        }
    }
}
