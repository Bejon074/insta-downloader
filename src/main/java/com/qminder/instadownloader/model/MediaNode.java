package com.qminder.instadownloader.model;

import lombok.Data;

import java.util.Date;

@Data
public class MediaNode {
    private String id;
    private String display_src;
    private boolean is_video;
    private Date date;
}
