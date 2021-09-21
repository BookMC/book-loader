package org.bookmc.loader.api.loader;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;
import org.bookmc.loader.api.config.LoaderConfig;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.exception.LoaderException;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.resolution.ModResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BookLoaderBase {
    private final Path modsPath;
    private final Path configPath;

    public BookLoaderBase(Path modsPath, Path configPath) {
        this.modsPath = modsPath;
        this.configPath = configPath;
        try {
            if (!Files.exists(modsPath)) {
                Files.createDirectory(modsPath);
            }
            if (!Files.exists(configPath)) {
                Files.createDirectory(configPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<ModResolver> resolvers = new ArrayList<>();
    protected Map<String, ModContainer> containers = new HashMap<>();
    protected List<String> loaded = new ArrayList<>();

    public static BookLoaderBase INSTANCE;

    public abstract void preload(LoaderConfig config);

    public abstract void load() throws LoaderException;

    public abstract Path getWorkingDirectory();

    public abstract GameEnvironment getGlobalEnvironment();

    public abstract AbstractBookURLClassLoader getGlobalClassLoader();

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
