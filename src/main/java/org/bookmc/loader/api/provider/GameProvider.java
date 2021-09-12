package org.bookmc.loader.api.provider;

import org.bookmc.loader.impl.provider.ArgumentHandler;

import java.io.File;

public interface GameProvider {
    void load(ArgumentHandler handler);

    String getLaunchedVersion();

    File getAssetsDirectory();

    File getGameDirectory();

    String getLaunchTarget();
}
