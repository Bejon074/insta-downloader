package com.qminder.instadownloader.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Constraint;

@Data
@Entity
public class UserDetail {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    @Column
    private String fullName;

    @Column
    private String lastDownloadedFileId;

    @Column
    private int totalFileDownloaded;

    @Column
    private String fileSavingDirectory;
}
