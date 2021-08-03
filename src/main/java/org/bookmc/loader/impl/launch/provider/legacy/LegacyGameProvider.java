package org.bookmc.loader.impl.launch.provider.legacy;

import org.bookmc.loader.impl.launch.provider.ArgumentHandler;
import org.bookmc.loader.impl.launch.provider.GameProvider;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.io.File;

public class LegacyGameProvider implements GameProvider {
    private final ArgumentHandler handler;

    private String launchedVersion;
    private File assetsDirectory;
    private File gameDirectory;

    public LegacyGameProvider(ArgumentHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getLaunchedVersion() {
        if (launchedVersion == null) {
            launchedVersion = handler.get("version")
                .orElseThrow(() -> new IllegalStateException("The game was not launched correctly! (Could not find version argument)"));
        }

        return launchedVersion;
    }

    @Override
    public File getAssetsDirectory() {
        if (assetsDirectory == null) {
            String dir = handler.get("assetsDir")
                .orElseThrow(() -> new IllegalStateException("The game was not launched correctly (Could not find the assetsDir argument)"));

            assetsDirectory = new File(dir);
        }

        return assetsDirectory;
    }

    @Override
    public File getGameDirectory() {
        if (gameDirectory == null) {
            gameDirectory = new File(handler.get("gameDir").orElse("."));
        }

        return gameDirectory;
    }

    @Override
    public String getLaunchTarget() {
        return MixinBootstrap.getPlatform().getLaunchTarget();
    }
}
