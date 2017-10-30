package com.qminder.instadownloader.service;

import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDetail> getProfiles() {
        return userRepository.findAll();
    }

    @Override
    public UserDetail getUserDetailByName(String name) {
        return userRepository.findUserDetailByUserName(name);
    }

    @Transactional
    @Override
    public UserDetail saveUserDetail(UserDetail userDetail) {
        return userRepository.saveAndFlush(userDetail);
    }
}
