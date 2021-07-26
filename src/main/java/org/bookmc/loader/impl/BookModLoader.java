package org.bookmc.loader.impl;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.discoverer.DevelopmentModDiscoverer;
import org.bookmc.loader.impl.ui.MissingDependencyUI;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookModLoader {
    private static final Logger logger = LogManager.getLogger();
    public static final List<ModVessel> loaded = new ArrayList<>();

    private static final Map<String, ArrayList<String>> missingDependencies = new HashMap<>();

    private static final Object object = new Object();

    public static void load() {
        for (ModVessel vessel : Loader.getModVessels()) {
            if (loaded.contains(vessel)) {
                continue;
            }

            loadDependencies(vessel);
            // In the chances of someone for some stupid reason decided to add their own mod as a dependency
        }

        for (ModVessel vessel : Loader.getModVessels()) {
            if (!missingDependencies.isEmpty()) {
                try {
                    synchronized (object) {
                        MissingDependencyUI.failed(missingDependencies);
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new IllegalStateException("Something went wrong when attempting to block the thread!");
            }

            if (!loaded.contains(vessel)) {
                load(vessel);
            }
        }
    }

    private static void loadDependencies(ModVessel vessel) {
        ArrayList<String> list = missingDependencies.getOrDefault(vessel.getId(), new ArrayList<>());
        for (String dependency : vessel.getDependencies()) {
            boolean isFound;

            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency);

            isFound = dependencyVessel != null;

            if (isFound) {
                loadDependencies(dependencyVessel);
                load(dependencyVessel);
            }

            if (!isFound) {
                list.add(dependency);
            }
        }

        if (!list.isEmpty()) {
            missingDependencies.put(vessel.getId(), list);
        }
    }

    private static void load(ModVessel vessel) {
        if (!vessel.isInternallyEnabled() || vessel.isCompatabilityLayer()) return;
        String[] split = vessel.getEntrypoint().split("::");

        Class<?> entryClass = null;

        try {
            entryClass = Class.forName(split[0], true, Launch.classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        loaded.add(vessel);
        try {
            if (entryClass != null) {
                logger.debug("Loading " + vessel.getName() + " from " + vessel.getEntrypoint());
                entryClass.getDeclaredMethod(split[1]).invoke(entryClass.getConstructor().newInstance());
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void reload(File directory) {
        for (MinecraftModDiscoverer discoverer : Loader.getModDiscoverers()) {
            File[] files = directory.listFiles();

            if (files != null || discoverer instanceof DevelopmentModDiscoverer) {
                discoverer.discover(files);
            }
        }

        load();
    }
}
