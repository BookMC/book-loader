package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.api.mod.resolution.ResolverService;

import java.nio.file.Path;
import java.util.ServiceLoader;

public class BookLoaderImpl extends BookLoaderBase {
    private final Path workingDirectory;
    private final GameEnvironment gameEnvironment;
    private final AbstractBookURLClassLoader parentClassLoader;

    public BookLoaderImpl(Path workingDirectory, GameEnvironment gameEnvironment, AbstractBookURLClassLoader parentClassLoader) {
        this.workingDirectory = workingDirectory;
        this.gameEnvironment = gameEnvironment;
        this.parentClassLoader = parentClassLoader;
    }

    @Override
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public GameEnvironment getGameEnvironment() {
        return gameEnvironment;
    }

    @Override
    public void preload() {
        for (ResolverService service : ServiceLoader.load(ResolverService.class)) {
            for (ModResolver resolver : service.getModResolvers()) {
                addResolver(resolver);
            }
        }

        loadCandidates();
    }

    @Override
    public void load() {

    }

    private void loadCandidates() {
        for (ModResolver resolver : resolvers) {
            ModCandidate[] candidates = resolver.resolveMods();
            for (ModCandidate candidate : candidates) {
                if (candidate.validate()) {
                    candidate.loadContainers(parentClassLoader);
                    for (ModContainer container : candidate.getContainers()) {
                        container.createModResource("").getResourceAsStream();
                    }
                }
            }
        }
    }
}