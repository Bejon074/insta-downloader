package com.qminder.instadownloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InstaDownloaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstaDownloaderApplication.class, args);
    }
}
