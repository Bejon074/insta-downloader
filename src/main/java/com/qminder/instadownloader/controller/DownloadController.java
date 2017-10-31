package com.qminder.instadownloader.controller;


import com.qminder.instadownloader.domain.UserDetail;
import com.qminder.instadownloader.helper.ReturnedMassage;
import com.qminder.instadownloader.model.DownloadRequest;
import com.qminder.instadownloader.service.DownloadService;
import com.qminder.instadownloader.service.PathResolverService;
import com.qminder.instadownloader.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Controller
public class DownloadController {

    @Autowired
    private PathResolverService pathResolverService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private Instagram instagram;

    @RequestMapping(name = "/download", method = RequestMethod.POST)
    public ModelAndView downloadAllImages(@Valid DownloadRequest downloadRequest) {
        log.info("download request: {}", downloadRequest);
        ModelAndView modelAndView = new ModelAndView("index");
        if (pathResolverService.isPathValid(downloadRequest.getDirectory())) {
            Account account;
            try {
                account = instagram.getAccountByUsername(downloadRequest.getUserName());
                UserDetail userDetail = profileService.getUserDetailByName(downloadRequest.getUserName());
                downloadService.startDownload(account, downloadRequest.getDirectory(), userDetail, true);
                modelAndView.addObject("successMsg", ReturnedMassage.NewDownloadSuccess);
            } catch (Exception ex) {
                modelAndView.addObject("errorMsg", ReturnedMassage.NoAccountFound);
            }
        } else {
            modelAndView.addObject("errorMsg", ReturnedMassage.PathResolvedError);
        }
        List<UserDetail> userDetails = profileService.getProfiles();
        modelAndView.addObject("userDetails", userDetails);
        modelAndView.addObject("downloadRequest", new DownloadRequest());
        return modelAndView;
    }
}
