package org.bookmc.loader;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.book.DevelopmentModDiscoverer;
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
            this.args.add("--gameDir");
            this.args.add(gameDir.getAbsolutePath());
        }

        if (assetsDir != null) {
            this.args.add("--assetsDir");
            this.args.add(assetsDir.getAbsolutePath());
        }

        if (profile != null) {
            this.args.add("--version");
            this.args.add(profile);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.addTransformerExclusion("org.bookmc.loader."); // Disallow transformation of mod loading

        MixinBootstrap.init();

        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);

        for (MinecraftModDiscoverer discoverer : Loader.getModDiscoverers()) {
            File[] files = new File(Launch.minecraftHome, "mods").listFiles();

            if (files != null || discoverer instanceof DevelopmentModDiscoverer) {
                discoverer.discover(files);
            }
        }

        for (ModVessel vessel : Loader.getModVessels()) {
            String mixinEntrypoint = vessel.getMixinEntrypoint();

            if (mixinEntrypoint != null) {
                Mixins.addConfiguration(mixinEntrypoint);
            }
        }

        Mixins.addConfiguration("bookmc-client.mixins.json");

        boolean isTransformationServiceAvailable = false;

        try {
            Class.forName("org.bookmc.services.TransformationService");
            isTransformationServiceAvailable = true;
        } catch (ClassNotFoundException ignored) {

        }

        if (isTransformationServiceAvailable) {
            classLoader.registerTransformer("org.bookmc.services.TransformationService");
        }
    }

    @Override
    public String getLaunchTarget() {
        String version = args.get(args.indexOf("--version") + 1);
        if (!version.equals("1.8.9")) {
            throw new IllegalStateException("Unknown version was launched");
        }

        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(new String[0]);
    }
}
