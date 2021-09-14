package org.bookmc.loader.api.provider;

import org.bookmc.loader.impl.provider.ArgumentHandler;

import java.io.File;
import java.nio.file.Path;

public interface GameProvider {
    void load(ArgumentHandler handler);

    String getLaunchedVersion();

    @Deprecated
    File getAssetsDirectory();

    @Deprecated
    File getGameDirectory();

    default Path getAssetsDirectoryPath() {
        return getAssetsDirectory().toPath();
    }

    default Path getGameDirectoryPath() {
        return getGameDirectory().toPath();
    }

    String getLaunchTarget();
}
