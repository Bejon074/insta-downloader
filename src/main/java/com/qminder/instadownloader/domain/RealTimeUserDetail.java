package com.qminder.instadownloader.domain;

import com.qminder.instadownloader.Enum.DownloadType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class RealTimeUserDetail {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    @Column
    private String fullName;

    @Column
    private String maxId;

    @Column
    private String fileSavingDirectory;

    @Enumerated(value = EnumType.ORDINAL)
    @Column
    private DownloadType downloadType;
}
