package org.bookmc.loader.impl.launch.provider;

import java.io.File;

public interface GameProvider {
    String getLaunchedVersion();

    File getAssetsDirectory();

    File getGameDirectory();

    String getLaunchTarget();
}
