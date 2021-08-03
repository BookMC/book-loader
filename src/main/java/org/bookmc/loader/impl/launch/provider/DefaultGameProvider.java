package org.bookmc.loader.impl.launch.provider;

import java.io.File;

public class DefaultGameProvider implements GameProvider {
    private final ArgumentHandler handler;

    private String version;

    private File assetsDirectory;
    private File gameDirectory;


    public DefaultGameProvider(ArgumentHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getLaunchedVersion() {
        if (version == null) {
            version = handler.get("version").orElseThrow(() -> new IllegalStateException("The game was not loaded correctly, could not detect version"));
        }

        return version;
    }

    @Override
    public File getAssetsDirectory() {
        if (assetsDirectory == null) {
            assetsDirectory = new File(handler.get("assetsDir").orElseThrow(() -> new IllegalStateException("The game was not loaded correctly, could not find assets directory")));
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
        return "net.minecraft.client.main.Main";
    }
}
