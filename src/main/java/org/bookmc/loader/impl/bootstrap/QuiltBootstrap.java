package org.bookmc.loader.impl.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.launch.BookLoader;
import org.bookmc.loader.impl.launch.BookLauncher;
import org.bookmc.loader.impl.mixin.BookMixinBootstrap;
import org.bookmc.loader.impl.resolve.BookModResolver;
import org.bookmc.loader.impl.vessel.dummy.BookLoaderVessel;
import org.bookmc.loader.impl.vessel.dummy.JavaModVessel;
import org.bookmc.loader.impl.vessel.dummy.MinecraftModVessel;
import org.bookmc.loader.impl.vessel.dummy.candidate.FakeCandidate;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The Quilt Bootstrap allows is the biggest part of the loader.
 * It holds the code to successfully start mod loading by bootstrapping
 * libraries such as Mixin and discovering mods. It also allows external
 * developers to bootstrap the loader without making errors trying to abstract
 * code baked into {@link BookLoader} and without using
 * {@link BookLoader} itself.
 */
public class QuiltBootstrap {
    private static final Logger LOGGER = LogManager.getLogger(QuiltBootstrap.class);

    public static void plug() {
        Path modsDirectory = BookLauncher.getModsFolder();
        File modDirectoryFile = modsDirectory.toFile();

        if (!modDirectoryFile.exists()) {
            if (!modDirectoryFile.mkdir()) {
                LOGGER.fatal("Failed to create mods directory");
            }
        }

        String version = BookLauncher.getGameProvider().getLaunchedVersion();
        LOGGER.info("Initializating book-loader, provided game version: {}", version);

        LOGGER.info("Registered resolver for {}", modsDirectory);
        Loader.registerResolver(new BookModResolver(modsDirectory));
        Path versionPath = modsDirectory.resolve(version);
        LOGGER.info("Registered resolver for {}", versionPath.toFile().getAbsolutePath());
        Loader.registerResolver(new BookModResolver(versionPath));

        ModVessel[] fakeContainers = new ModVessel[]{new MinecraftModVessel(version), new JavaModVessel(), new BookLoaderVessel()};

        String fakeContainerNames = String.join(
            ", ",
            Arrays.stream(fakeContainers)
                .map(ModVessel::getName)
                .collect(Collectors.toSet())
        );

        LOGGER.info("Registering fake containers ({})", fakeContainerNames);
        Loader.registerCandidate(new FakeCandidate(fakeContainers));

        try {
            Loader.discoverAndLoad();
        } catch (IllegalDependencyException e) {
            e.printStackTrace();
        }

        // We have to bootstrap mixin after all the mods have been discovered
        // so that mods can have their mixin files discovered.
        LOGGER.info("Initializing Mixin Bootstrap");
        BookMixinBootstrap.init();
    }
}
