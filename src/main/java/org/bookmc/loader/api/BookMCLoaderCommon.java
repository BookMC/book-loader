package org.bookmc.loader.api;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.compat.CompatabilityLayer;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.BookModLoader;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.dummy.JavaModVessel;
import org.bookmc.loader.impl.dummy.MinecraftModVessel;
import org.bookmc.loader.utils.ClassUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BookMCLoaderCommon implements ITweaker {
    private static File modsDirectory;
    private static MixinEnvironment.Side side = MixinEnvironment.Side.UNKNOWN;
    private final Logger logger = LogManager.getLogger(this);
    private final List<String> args = new ArrayList<>();
    private String version;

    public static File getModsDirectory() {
        return modsDirectory;
    }

    public static MixinEnvironment.Side getSide() {
        return side;
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args.addAll(args);

        if (gameDir != null) {
            addArg("gameDir", gameDir.getAbsolutePath());
        }

        if (assetsDir != null) {
            addArg("assetsDir", assetsDir.getAbsolutePath());
        }

        if (profile != null) {
            addArg("version", profile);
            this.version = profile;
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // Redirect this stuff to the parent classloader
        classLoader.addClassLoaderExclusion("org.bookmc.loader.");

        MixinBootstrap.init();

        MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();

        injectIntoClassLoader(classLoader, environment);

        String passedDirectory = System.getProperty("book.discovery.folder", "mods");

        modsDirectory = new File(Launch.minecraftHome, passedDirectory);

        if (!modsDirectory.exists()) {
            if (!modsDirectory.mkdir()) {
                System.err.println("Failed to create mods directory");
            }
        }

        Loader.registerVessel(new JavaModVessel());
        Loader.registerVessel(new MinecraftModVessel(version));

        try {
            loadModMixins(modsDirectory, classLoader);
        } catch (IllegalDependencyException e) {
            e.printStackTrace();
        }

        if (version != null) {
            try {
                loadModMixins(new File(modsDirectory, version), classLoader);
            } catch (IllegalDependencyException e) {
                e.printStackTrace();
            }
            Loader.registerVessel(new MinecraftModVessel(version));
        } else {
            logger.error("Failed to detect the game version! Mods inside the game version's mod folder will not be loaded!");
        }

        if (environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("notch"); // Switch's to notch mappings
        }

        // Load our transformation service only if it's available.
        if (ClassUtils.isClassAvailable("org.bookmc.services.TransformationService")) {
            classLoader.registerTransformer("org.bookmc.services.TransformationService");
        }

        side = setSide(environment);
    }

    public abstract void injectIntoClassLoader(LaunchClassLoader classLoader, MixinEnvironment environment);

    public abstract MixinEnvironment.Side setSide(MixinEnvironment environment);

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(new String[0]);
    }

    private void addArg(String key, String value) {
        args.add("--" + key);
        args.add(value);
    }

    private void loadModMixins(File modsDirectory, LaunchClassLoader classLoader) throws IllegalDependencyException {
        Loader.discover(modsDirectory);

        for (ModVessel vessel : Loader.getModVessels()) {
            if (vessel.isInternallyEnabled()) {
                try {
                    String entrypoint = vessel.getEntrypoint();
                    if (!entrypoint.contains("::")) {
                        Class<?> clazz = Class.forName(entrypoint, false, classLoader);

                        if (clazz.isAssignableFrom(CompatabilityLayer.class)) {
                            if (vessel.getDependencies().length != 0) {
                                throw new IllegalDependencyException(vessel);
                            }

                            BookModLoader.loaded.add(vessel); // Trick BookModLoader#load to believe we have "loaded" our "mod".
                            CompatabilityLayer layer = (CompatabilityLayer) clazz.newInstance();
                            layer.init(this, classLoader);
                        }
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {

                }

                String mixinEntrypoint = vessel.getMixinEntrypoint();
                // Load mixins from everywhere (All jars should now be on the LaunchClassLoader)
                if (mixinEntrypoint != null) {
                    Mixins.addConfiguration(mixinEntrypoint);
                }
            }
        }
    }
}
