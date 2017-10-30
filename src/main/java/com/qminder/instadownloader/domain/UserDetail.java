package com.qminder.instadownloader.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserDetail {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String fullName;

    @Column
    private String lastDownloadedFileId;

    @Column
    private Long totalFileDownloaded;
}
