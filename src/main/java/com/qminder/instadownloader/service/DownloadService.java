package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.UserDetail;

import java.nio.file.Path;

public interface DownloadService {

    void startNewDownload(String userName, String directory);
    void startDownload(String userName,
                              String maxId,
                              Path path,
                              String directory,
                              UserDetail savedUserDetail);
}
