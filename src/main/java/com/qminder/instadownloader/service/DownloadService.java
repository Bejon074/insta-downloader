package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.UserDetail;
import me.postaddict.instagram.scraper.domain.Account;

public interface DownloadService {

    void startDownload(Account account, String directory, UserDetail savedUserDetail, boolean isNewDownload);
}
