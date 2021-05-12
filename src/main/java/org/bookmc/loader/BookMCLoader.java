package org.bookmc.loader;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookMCLoader implements ITweaker {
    private final List<String> args = new ArrayList<>();

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args.addAll(args);
        this.args.add("--gameDir");
        this.args.add(gameDir.getAbsolutePath());

        this.args.add("--assetsDir");
        this.args.add(assetsDir.getAbsolutePath());

        this.args.add("--version");
        this.args.add(profile);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer("org.bookmc.services.TransformationService");
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
