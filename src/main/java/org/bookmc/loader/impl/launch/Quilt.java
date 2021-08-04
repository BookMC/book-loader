package org.bookmc.loader.impl.launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.launch.provider.ArgumentHandler;
import org.bookmc.loader.impl.launch.provider.DefaultGameProvider;
import org.bookmc.loader.impl.launch.transform.QuiltClassLoader;
import org.bookmc.loader.impl.vessel.dummy.BookLoaderVessel;
import org.bookmc.loader.impl.vessel.dummy.JavaModVessel;
import org.bookmc.loader.impl.vessel.dummy.MinecraftModVessel;
import org.bookmc.loader.impl.vessel.dummy.candidate.DummyCandidate;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.lang.reflect.Method;

public class Quilt {
    static QuiltClassLoader classLoader;
    private final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Throwable {
        classLoader = new QuiltClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        ArgumentHandler handler = new ArgumentHandler(args);
        new Quilt().launch(args, handler);
    }

    private void launch(String[] args, ArgumentHandler handler) throws Throwable {
        Launcher.setGameProvider(new DefaultGameProvider(handler));

        String target = handler.get("target")
            .orElse(Launcher.getGameProvider().getLaunchTarget());

        File modsDirectory = Launcher.getModsFolder();

        if (!modsDirectory.exists()) {
            if (!modsDirectory.mkdir()) {
                System.err.println("Failed to create mods directory");
            }
        }


        Loader.registerCandidate(new DummyCandidate(new ModVessel[]{new MinecraftModVessel(Launcher.getGameProvider().getLaunchedVersion()), new JavaModVessel(), new BookLoaderVessel()}));

        try {
            Loader.discoverAndLoad(modsDirectory, Launcher.getEnvironment());
        } catch (IllegalDependencyException e) {
            e.printStackTrace();
        }

        if (Launcher.getGameProvider().getLaunchedVersion() != null) {
            try {
                Loader.discoverAndLoad(new File(modsDirectory, Launcher.getGameProvider().getLaunchedVersion()), Launcher.getEnvironment());
            } catch (IllegalDependencyException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Failed to detect the game version! Mods inside the game version's mod folder will not be loaded!");
        }

        // We have to bootstrap mixin after all the mods have been discovered
        // so that mods can have their mixin files discovered.
        boostrapMixin();

        MixinEnvironment mixinEnvironment = MixinEnvironment.getDefaultEnvironment();

        Loader.loadMixins(Launcher.getEnvironment());

        if (mixinEnvironment.getObfuscationContext() == null) {
            mixinEnvironment.setObfuscationContext("notch"); // Switch's to notch mappings
        }

        mixinEnvironment.setSide(Environment.toMixin(Launcher.getEnvironment()));

        Class<?> clazz = Class.forName(target, false, classLoader);
        Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
    }

    private void boostrapMixin() {
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
