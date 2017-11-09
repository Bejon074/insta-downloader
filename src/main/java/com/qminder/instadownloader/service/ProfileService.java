package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.RealTimeUserDetail;

import java.util.List;

public interface ProfileService {

    List<RealTimeUserDetail> getProfiles();

    RealTimeUserDetail getUserDetailByName(String name);

    RealTimeUserDetail saveUserDetail(RealTimeUserDetail userDetail);
}
