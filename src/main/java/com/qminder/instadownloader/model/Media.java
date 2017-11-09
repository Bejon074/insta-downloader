package com.qminder.instadownloader.model;

import lombok.Data;

import java.util.List;

@Data
public class Media {
    private List<MediaNode> nodes;
    private int count;
    private PageInfo page_info;
}
