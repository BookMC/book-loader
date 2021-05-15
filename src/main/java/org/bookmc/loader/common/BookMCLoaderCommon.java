package org.bookmc.loader.common;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.Loader;
import org.bookmc.loader.utils.ClassUtils;
import org.bookmc.loader.utils.DiscoveryUtils;
import org.bookmc.loader.vessel.ModVessel;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.MixinTweaker;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BookMCLoaderCommon implements ITweaker {
    private final MixinTweaker mixinTweaker = new MixinTweaker();

    private final List<String> args = new ArrayList<>();

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
        }
        mixinTweaker.acceptOptions(args, gameDir, assetsDir, profile);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        mixinTweaker.injectIntoClassLoader(classLoader);
        classLoader.addTransformerExclusion("org.bookmc.loader."); // Disallow transformation of mod loading

        MixinBootstrap.init();

        File modsDirectory = new File(Launch.minecraftHome, "mods");

        if (!modsDirectory.exists()) {
            if (!modsDirectory.mkdir()) {
                System.err.println("Failed to create mods directory");
            }
        }

        DiscoveryUtils.discover(modsDirectory);

        for (ModVessel vessel : Loader.getModVessels()) {
            String mixinEntrypoint = vessel.getMixinEntrypoint();

            // Load from development environment :)
            if (mixinEntrypoint != null && vessel.getClassLoader() == null) {
                Mixins.addConfiguration(mixinEntrypoint);
            }
        }

        // Load our transformation service only if it's available.
        if (ClassUtils.isClassAvailable("org.bookmc.services.TransformationService")) {
            classLoader.registerTransformer("org.bookmc.services.TransformationService");
        }
    }

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(new String[0]);
    }

    private void addArg(String key, String value) {
        args.add("--" + key);
        args.add(value);
    }
}
