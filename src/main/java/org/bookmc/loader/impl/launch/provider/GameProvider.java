package org.bookmc.loader.impl.launch.provider;

import java.io.File;
import java.util.ServiceLoader;

public interface GameProvider {
    static GameProvider findService() {
        return ServiceLoader.load(GameProvider.class).iterator().next();
    }

    String getLaunchedVersion();

    File getAssetsDirectory();

    File getGameDirectory();

    String getLaunchTarget();
}
