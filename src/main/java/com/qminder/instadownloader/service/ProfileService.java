package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.UserDetail;

import java.util.List;

public interface ProfileService {

    List<UserDetail> getProfiles();

    UserDetail getUserDetailByName(String name);

    UserDetail saveUserDetail(UserDetail userDetail);
}
