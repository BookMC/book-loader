package org.bookmc.loader.impl.launch.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.resolve.BookModResolver;
import org.bookmc.loader.impl.vessel.dummy.BookLoaderVessel;
import org.bookmc.loader.impl.vessel.dummy.JavaModVessel;
import org.bookmc.loader.impl.vessel.dummy.MinecraftModVessel;
import org.bookmc.loader.impl.vessel.dummy.candidate.DummyCandidate;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.lang.reflect.Method;

/**
 * The Quilt Bootstrap allows is the biggest part of the loader.
 * It holds the code to successfully start mod loading by bootstrapping
 * libraries such as Mixin and discovering mods. It also allows external
 * developers to bootstrap the loader without making errors trying to abstract
 * code baked into {@link org.bookmc.loader.impl.launch.Quilt} and without using
 * {@link org.bookmc.loader.impl.launch.Quilt} itself.
 */
public class QuiltBootstrap {
    private static final Logger LOGGER = LogManager.getLogger(QuiltBootstrap.class);

    public static void plug() {
        File modsDirectory = Launcher.getModsFolder();

        if (!modsDirectory.exists()) {
            if (!modsDirectory.mkdir()) {
                System.err.println("Failed to create mods directory");
            }
        }

        String version = Launcher.getGameProvider().getLaunchedVersion();

        Loader.registerResolver(new BookModResolver(new File(modsDirectory, "mods")));
        Loader.registerResolver(new BookModResolver(new File(modsDirectory, "mods/" + version)));

        Loader.registerCandidate(new DummyCandidate(new ModVessel[]{new MinecraftModVessel(version), new JavaModVessel(), new BookLoaderVessel()}));

        try {
            Loader.discoverAndLoad();
        } catch (IllegalDependencyException e) {
            e.printStackTrace();
        }

        // We have to bootstrap mixin after all the mods have been discovered
        // so that mods can have their mixin files discovered.
        boostrapMixin();

        MixinEnvironment mixinEnvironment = MixinEnvironment.getDefaultEnvironment();

        Loader.loadMixins(Launcher.getEnvironment());

        if (!Launcher.isDevelopment()) {
            if (mixinEnvironment.getObfuscationContext() == null) {
                mixinEnvironment.setObfuscationContext("searge"); // Switch's to notch mappings
            }
        }

        mixinEnvironment.setSide(Environment.toMixin(Launcher.getEnvironment()));

    }

    private static void boostrapMixin() {
        MixinBootstrap.init();
        try {
            Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, MixinEnvironment.Phase.INIT);
            m.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            LOGGER.fatal("Mixin error!");
            throw new RuntimeException("Failed to bootstrap Mixin", e);
        }
    }
}
