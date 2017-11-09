package com.qminder.instadownloader.helper;

public interface Constants {

    int downloadChunkSize = 20;
    int schedulerChunkSize = 100;
    int poolingTimeGap = 10 * 60 * 1000;
    String mediaURL = "https://www.instagram.com/{userName}/?__a=1&max_id=";
}
