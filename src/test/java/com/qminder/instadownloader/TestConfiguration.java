package com.qminder.instadownloader;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.qminder.instadownloader.service", "com.qminder.instadownloader.config"})
public class TestConfiguration {

}
