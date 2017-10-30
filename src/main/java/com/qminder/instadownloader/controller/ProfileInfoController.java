package com.qminder.instadownloader.controller;

import com.qminder.instadownloader.controller.model.DownloadRequest;
import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ProfileInfoController {

    @Autowired
    private ProfileService profileService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView getAllProfiles() {
        List<UserDetail> userDetails = profileService.getProfiles();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("userDetails", userDetails);
        modelAndView.addObject("downloadRequest", new DownloadRequest());
        return modelAndView;
    }
}
