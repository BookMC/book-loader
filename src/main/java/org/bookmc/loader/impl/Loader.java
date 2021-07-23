package org.bookmc.loader.impl;

import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.discoverer.BookModDiscoverer;
import org.bookmc.loader.impl.discoverer.ClasspathModDiscoverer;
import org.bookmc.loader.impl.discoverer.DevelopmentModDiscoverer;

import java.io.File;
import java.util.*;

public class Loader {
    private static final List<MinecraftModDiscoverer> discoverers = new ArrayList<>();
    private static final Map<String, ModVessel> modVessels = new HashMap<>();

    static {
        // Register default mod loader.
        Loader.registerModDiscoverer(new BookModDiscoverer());
        Loader.registerModDiscoverer(new ClasspathModDiscoverer());
        Loader.registerModDiscoverer(new DevelopmentModDiscoverer());
    }

    public static void registerModDiscoverer(MinecraftModDiscoverer minecraftModDiscoverer) {
        discoverers.add(minecraftModDiscoverer);
    }

    public static List<MinecraftModDiscoverer> getModDiscoverers() {
        return Collections.unmodifiableList(discoverers);
    }

    public static void registerVessel(ModVessel vessel) {
        modVessels.put(vessel.getId(), vessel);
    }

    public static List<ModVessel> getModVessels() {
        return Collections.unmodifiableList(new ArrayList<>(modVessels.values()));
    }

    public static Map<String, ModVessel> getModVesselsMap() {
        return Collections.unmodifiableMap(modVessels);
    }

    public static void discover(File modsDirectory) {
        for (MinecraftModDiscoverer discoverer : Loader.getModDiscoverers()) {
            File[] files = modsDirectory.listFiles();

            if (files != null || !discoverer.isFilesRequired()) {
                discoverer.discover(files);
            }
        }
    }
}
