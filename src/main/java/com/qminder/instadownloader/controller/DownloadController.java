package com.qminder.instadownloader.controller;


import com.qminder.instadownloader.controller.model.DownloadRequest;
import com.qminder.instadownloader.helper.ReturnedMassage;
import com.qminder.instadownloader.service.DownloadService;
import com.qminder.instadownloader.service.PathResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DownloadController {

    @Autowired
    private PathResolverService pathResolverService;

    @Autowired
    private DownloadService downloadService;

    @RequestMapping(name = "/download", method = RequestMethod.POST)
    public String downloadAllImages(@RequestBody @Valid DownloadRequest downloadRequest) {

        if(pathResolverService.isPathValid(downloadRequest.getDirectory())){
            downloadService.startNewDownload(downloadRequest.getUserName(), downloadRequest.getDirectory());
            return ReturnedMassage.NewDownloadSuccess;
        }else{
            return ReturnedMassage.PathResolvedError;
        }
    }
}
