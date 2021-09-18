package org.bookmc.loader.api.loader;

import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.resolution.ModResolver;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BookLoaderBase {
    private final Path modsPath = getWorkingDirectory().resolve("mods");
    private final Path configPath = getWorkingDirectory().resolve("config");

    protected List<ModResolver> resolvers = new ArrayList<>();
    protected Map<String, ModContainer> containers = new HashMap<>();

    public static BookLoaderBase INSTANCE;

    public abstract void preload();
    public abstract void load();

    public abstract Path getWorkingDirectory();

    public abstract GameEnvironment getGameEnvironment();

    public Path getModsPath() {
        return modsPath;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public void addResolver(ModResolver resolver) {
        resolvers.add(resolver);
    }

    public List<ModResolver> getResolvers() {
        return resolvers;
    }

    public Map<String, ModContainer> getContainers() {
        return containers;
    }
}
