package com.qminder.instadownloader.config;

import okhttp3.OkHttpClient;
import me.postaddict.instagram.scraper.Instagram;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configurations {

    @Bean
    public Instagram getInstagram(){
        return new Instagram(new OkHttpClient());
    }
}
