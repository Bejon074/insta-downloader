package com.qminder.instadownloader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramUser {

    private String biography;
    private String full_name;
    private String id;
    private String username;
    private Media media;
}
