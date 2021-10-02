package org.bookmc.test.service;

import org.bookmc.loader.api.service.GameDataService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class TestGameDataService implements GameDataService {
    @Override
    public String getGameName() {
        return "Minecraft";
    }

    @Override
    public String getGameVersion() {
        return "1.17.1";
    }

    @Override
    public String getGameEntrypoint() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public Path getWorkingDirectory(Map<String, String> arguments) {
        return Paths.get(arguments.getOrDefault("gameDir", System.getProperty("user.dir")));
    }
}
