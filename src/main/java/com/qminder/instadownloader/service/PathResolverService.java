package com.qminder.instadownloader.service;

import java.nio.file.Path;

public interface PathResolverService {

    boolean isPathValid(String directory);
    Path getPath(String directory, String userName);
}
