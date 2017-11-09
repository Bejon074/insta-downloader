package com.qminder.instadownloader.service;

import com.qminder.instadownloader.helper.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class PathResolverServiceImpl implements PathResolverService {


    @Override
    public boolean isPathValid(String directory) {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Path getPath(String directory, String userName) {
        Path path = Paths.get(directory + "\\" + userName);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error("Error Creating the path. {}", directory);
            }
        }
        return path;
    }

    @Override
    public String getUserMediaUrl(String userName, String maxId) {
        String genericUrl = Constants.mediaURL;
        return genericUrl.replace("{userName}",userName) + maxId;
    }
}
