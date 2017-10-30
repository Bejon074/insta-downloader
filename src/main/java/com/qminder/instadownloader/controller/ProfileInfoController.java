package com.qminder.instadownloader.controller;

import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProfileInfoController {

    @Autowired
    private ProfileService profileService;

    @RequestMapping(value = "get-profiles", method = RequestMethod.GET)
    public List<UserDetail> getAllProfiles(){
        return profileService.getProfiles();
    }
}
