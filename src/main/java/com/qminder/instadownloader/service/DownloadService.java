package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.RealTimeUserDetail;

public interface DownloadService {

    void startNewDownload(String userName, String directory);
    void handleScheduleDownload(RealTimeUserDetail realTimeUserDetails);
}
