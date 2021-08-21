package org.bookmc.loader.impl.launch.provider;

import java.io.File;

public interface GameProvider {
    void load(ArgumentHandler handler);

    String getLaunchedVersion();

    File getAssetsDirectory();

    File getGameDirectory();

    String getLaunchTarget();
}
