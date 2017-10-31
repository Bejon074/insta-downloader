package com.qminder.instadownloader.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class DownloadRequest {

    @NotNull
    @NotEmpty
    private String userName;

    @NotNull
    @NotEmpty
    private String directory;
}
