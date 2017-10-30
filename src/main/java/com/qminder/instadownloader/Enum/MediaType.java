package com.qminder.instadownloader.Enum;

public enum MediaType {
    VIDEO("video"),
    IMAGE("image");

    private String type;

    MediaType(String type) {
        this.type = type;
    }

    public String getValue() {
        return type;
    }
}
