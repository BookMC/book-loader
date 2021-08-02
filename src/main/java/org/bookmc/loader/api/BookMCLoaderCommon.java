package org.bookmc.loader.api;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.vessel.dummy.BookLoaderVessel;
import org.bookmc.loader.impl.vessel.dummy.JavaModVessel;
import org.bookmc.loader.impl.vessel.dummy.MinecraftModVessel;
import org.bookmc.loader.impl.vessel.dummy.candidate.DummyCandidate;
import org.bookmc.loader.shared.utils.ClassUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BookMCLoaderCommon implements ITweaker {
    private static File modsDirectory;
    private static Environment environment = Environment.UNKNOWN;
    private final Logger logger = LogManager.getLogger(this);
    private final List<String> args = new ArrayList<>();
    private String version;

    public static File getModsDirectory() {
        return modsDirectory;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public abstract Environment setEnvironment();

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args.addAll(args);

        if (gameDir != null) {
            this.args.addAll(Arrays.asList("--gameDir", gameDir.getAbsolutePath()));
        }

        if (assetsDir != null) {
            this.args.addAll(Arrays.asList("--assetsDir", assetsDir.getAbsolutePath()));
        }

        if (profile != null) {
            version = profile;
            this.args.addAll(Arrays.asList("--version", profile));
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        BookMCLoaderCommon.environment = setEnvironment();

        classLoader.addClassLoaderExclusion("org.bookmc.loader.");

        MixinBootstrap.init();

        MixinEnvironment mixinEnvironment = MixinEnvironment.getDefaultEnvironment();

        String passedDirectory = System.getProperty("book.discovery.folder", "mods");

        modsDirectory = new File(Launch.minecraftHome, passedDirectory);

        if (!modsDirectory.exists()) {
            if (!modsDirectory.mkdir()) {
                System.err.println("Failed to create mods directory");
            }
        }

        Loader.registerCandidate(new DummyCandidate(new ModVessel[]{new MinecraftModVessel(version), new JavaModVessel(), new BookLoaderVessel()}));

        try {
            Loader.discoverAndLoad(modsDirectory, environment);
        } catch (IllegalDependencyException e) {
            e.printStackTrace();
        }

        if (version != null) {
            try {
                Loader.discoverAndLoad(new File(modsDirectory, version), environment);
            } catch (IllegalDependencyException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("Failed to detect the game version! Mods inside the game version's mod folder will not be loaded!");
        }

        if (mixinEnvironment.getObfuscationContext() == null) {
            mixinEnvironment.setObfuscationContext("notch"); // Switch's to notch mappings
        }

        // Load our transformation service only if it's available.
        if (ClassUtils.isClassAvailable("org.bookmc.services.TransformationService")) {
            classLoader.registerTransformer("org.bookmc.services.TransformationService");
        }

        mixinEnvironment.setSide(Environment.toMixin(environment));
    }

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(new String[0]);
    }

    public String getVersion() {
        return version;
    }
}
