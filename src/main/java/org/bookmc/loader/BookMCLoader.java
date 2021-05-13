package org.bookmc.loader;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.utils.ClassUtils;
import org.bookmc.loader.utils.DiscoveryUtils;
import org.bookmc.loader.vessel.ModVessel;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookMCLoader implements ITweaker {
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
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
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

            if (mixinEntrypoint != null) {
                Mixins.addConfiguration(mixinEntrypoint);
            }
        }

        if (ClassUtils.isResourceAvailable("bookmc-client.mixins.json")) {
            Mixins.addConfiguration("bookmc-client.mixins.json");
            MixinEnvironment.getCurrentEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        }

        if (ClassUtils.isResourceAvailable("bookmc-server.mixins.json")) {
            Mixins.addConfiguration("bookmc-server.mixins.json");
            MixinEnvironment.getCurrentEnvironment().setSide(MixinEnvironment.Side.SERVER);
        }

        // Load our transformation service only if it's available.
        if (ClassUtils.isClassAvailable("org.bookmc.services.TransformationService")) {
            classLoader.registerTransformer("org.bookmc.services.TransformationService");
        }
    }

    @Override
    public String getLaunchTarget() {
        return ClassUtils.isResourceAvailable("bookmc-server.mixins.json") ? "net.minecraft.server.MinecraftServer" : "net.minecraft.client.main.Main";
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
