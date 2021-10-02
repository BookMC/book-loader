package org.bookmc.loader.api.loader;

import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.api.classloader.TransformableURLClassLoader;
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
    private final Path workingDirectory;
    private final Path modsPath;
    private final Path configPath;


    public BookLoaderBase(Path workingDirectory, Path modsPath, Path configPath) {
        this.workingDirectory = workingDirectory;
        this.modsPath = modsPath;
        this.configPath = configPath;
        try {
            if (!Files.exists(workingDirectory)) {
                Files.createDirectory(workingDirectory);
            }
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

    public abstract void preload();

    public abstract void load() throws LoaderException;

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public abstract GameEnvironment getGlobalEnvironment();

    public abstract AppendableURLClassLoader getGlobalClassLoader();

    public abstract LoaderConfig getLoaderConfig();

    public TransformableURLClassLoader getTransformClassLoader() {
        ClassLoader classLoader = getGlobalClassLoader();
        if (classLoader instanceof TransformableURLClassLoader) {
            return (TransformableURLClassLoader) classLoader;
        }
        throw new LoaderException("Provided classloader does not support transformation!");
    }

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
