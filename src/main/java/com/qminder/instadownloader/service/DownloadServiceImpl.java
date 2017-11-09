package com.qminder.instadownloader.service;

import com.qminder.instadownloader.Enum.DownloadType;
import com.qminder.instadownloader.domain.RealTimeUserDetail;
import com.qminder.instadownloader.model.InstagramResponse;
import com.qminder.instadownloader.model.MediaNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private PathResolverService pathResolverService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProfileService profileService;

    @Async("threadPoolTaskExecutor")
    @Override
    public void startNewDownload(String username, String directory) {
        RealTimeUserDetail realTimeUserDetail = profileService.getUserDetailByName(username);
        downloadAllFromMaxId(username, directory, realTimeUserDetail);
    }

    private void downloadAllFromMaxId(String username, String directory, RealTimeUserDetail realTimeUserDetail) {
        String maxId = realTimeUserDetail != null && realTimeUserDetail.getMaxId() != null ?
                realTimeUserDetail.getMaxId() : "";
        Path path = pathResolverService.getPath(directory, username);
        String url = pathResolverService.getUserMediaUrl(username, maxId);
        String fullName = "";
        try {
            InstagramResponse instagramResponse = restTemplate.getForObject(url, InstagramResponse.class);
            fullName = instagramResponse != null &&
                    instagramResponse.getUser() != null &&
                    instagramResponse.getUser().getFull_name() != null ?
                    instagramResponse.getUser().getFull_name() : "";
            log.info("instagramUser: {}", instagramResponse);
            while (instagramResponse != null &&
                    instagramResponse.getUser() != null &&
                    instagramResponse.getUser().getMedia() != null) {
                for (MediaNode mediaNode : instagramResponse.getUser().getMedia().getNodes()) {
                    if (!mediaNode.is_video()) {
                        try (InputStream in = new URL(mediaNode.getDisplay_src()).openStream()) {
                            log.info("downloading mediaNode: {}", mediaNode);
                            maxId = mediaNode.getId();
                            Files.copy(in, Paths.get(path + "\\" + mediaNode.getDisplay_src()
                                    .substring(mediaNode.getDisplay_src().lastIndexOf('/'))));
                        } catch (FileAlreadyExistsException ex) {
                            log.info("file already exits with id: {}", mediaNode.getId());
                        }
                    }
                }
                if (hasNext(instagramResponse)) {
                    url = pathResolverService.getUserMediaUrl(username, maxId);
                    instagramResponse = restTemplate.getForObject(url, InstagramResponse.class);
                } else {
                    realTimeUserDetail = saveUserDetails(realTimeUserDetail, username, instagramResponse.getUser().getFull_name(),
                            maxId, directory, DownloadType.ONLY_REALTIME);
                    break;
                }
            }
        } catch (Exception ex) {
            log.warn("May be Instagram blocked our request. No problem scheduler will start to do what we left");
            saveUserDetails(realTimeUserDetail, username, (fullName == "" ? username : fullName), maxId, directory, DownloadType.REALTIME_AND_PREVIOUS_ALL);
            log.error("error {}", ex);
        }
    }

    private boolean hasNext(InstagramResponse
                                    instagramResponse) {
        if (instagramResponse != null &&
                instagramResponse.getUser() != null &&
                instagramResponse.getUser().getMedia() != null &&
                instagramResponse.getUser().getMedia().getPage_info() != null) {
            return instagramResponse.getUser().getMedia().getPage_info().isHas_next_page();
        }
        return false;
    }

    private RealTimeUserDetail saveUserDetails(RealTimeUserDetail userDetails,
                                               String userName,
                                               String fullName,
                                               String maxId,
                                               String fileSavingDirectory,
                                               DownloadType downloadType) {
        RealTimeUserDetail realTimeUserDetail = userDetails == null ? new RealTimeUserDetail() : userDetails;
        realTimeUserDetail.setUserName(userName);
        realTimeUserDetail.setDownloadType(downloadType);
        realTimeUserDetail.setFullName(fullName);
        realTimeUserDetail.setMaxId(maxId);
        realTimeUserDetail.setFileSavingDirectory(fileSavingDirectory);
        return profileService.saveUserDetail(realTimeUserDetail);
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void handleScheduleDownload(RealTimeUserDetail realTimeUserDetail) {
        if(realTimeUserDetail.getDownloadType() == DownloadType.REALTIME_AND_PREVIOUS_ALL){
            downloadAllFromMaxId(realTimeUserDetail.getUserName(), realTimeUserDetail.getFileSavingDirectory(),
                    realTimeUserDetail);
        }
        downloadRecentImages(realTimeUserDetail);
    }
    
    private void downloadRecentImages(RealTimeUserDetail realTimeUserDetail){
        String maxId = "";
        Path path = pathResolverService.getPath(realTimeUserDetail.getFileSavingDirectory(),
                realTimeUserDetail.getUserName());
        String url = pathResolverService.getUserMediaUrl(realTimeUserDetail.getUserName(), maxId);
        try {
            InstagramResponse instagramResponse = restTemplate.getForObject(url, InstagramResponse.class);
            log.info("instagramUser: {}", instagramResponse);
            while (instagramResponse != null &&
                    instagramResponse.getUser() != null &&
                    instagramResponse.getUser().getMedia() != null) {
                boolean isUptoDate = false;
                for (MediaNode mediaNode : instagramResponse.getUser().getMedia().getNodes()) {
                    if (!mediaNode.is_video()) {
                        try (InputStream in = new URL(mediaNode.getDisplay_src()).openStream()) {
                            log.info("downloading mediaNode: {}", mediaNode);
                            Files.copy(in, Paths.get(path + "\\" + mediaNode.getDisplay_src()
                                    .substring(mediaNode.getDisplay_src().lastIndexOf('/'))));
                            maxId = mediaNode.getId();
                        } catch (FileAlreadyExistsException ex) {
                            isUptoDate = true;
                            break;
                        }
                    }
                }
                if(isUptoDate){
                    log.info("{} is upto date", realTimeUserDetail.getUserName());
                    break;
                }
                if (hasNext(instagramResponse)) {
                    url = pathResolverService.getUserMediaUrl(realTimeUserDetail.getUserName(), maxId);
                    instagramResponse = restTemplate.getForObject(url, InstagramResponse.class);
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            log.warn("<<<<<<<< RealTime download interrupted >>>>>>>");
            log.error("error {}", ex);
        }
    }

    @Override
    public boolean isUserExists(String username) {
        try{
            String url = pathResolverService.getUserMediaUrl(username, "");
            restTemplate.getForObject(url, InstagramResponse.class);
            return true;
        }catch (Exception ex){
            log.info("No User Exists with this given username: {}", username);
            log.error("Exception: {}",ex);
            return false;
        }
    }
}
