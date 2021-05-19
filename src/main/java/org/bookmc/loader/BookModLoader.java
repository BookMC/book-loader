package org.bookmc.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.exception.MissingEntrypointException;
import org.bookmc.loader.ui.MissingDependencyUI;
import org.bookmc.loader.vessel.ModVessel;
import org.bookmc.loader.vessel.json.library.LibraryModVessel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookModLoader {
    private static final Logger logger = LogManager.getLogger();
    private static final List<ModVessel> loaded = new ArrayList<>();

    private static final Map<String, ArrayList<String>> missingDependencies = new HashMap<>();

    private static final Object object = new Object();

    public static void load() {
        // Load libraries before mods
        for (ModVessel vessel : Loader.getLibrariesVessels()) {
            if (loaded.contains(vessel)) {
                continue;
            }

            loadDependencies(vessel);
            // In the chances of someone for some stupid reason decided to add their own mod as a dependency
        }

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

            if (dependencyVessel != null) {
                loadDependencies(dependencyVessel);
                load(dependencyVessel);
                isFound = true;
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
        if (!(vessel instanceof LibraryModVessel) && vessel.getEntrypoint() == null) {
            throw new MissingEntrypointException("You must specify an entrypoint. If this is a mistake specify your mod as a library by adding \"library\": true to your book.mod.json file");
        }

        if (!(vessel instanceof LibraryModVessel)) {
            String[] split = vessel.getEntrypoint().split("::");

            Class<?> entryClass = null;

            try {
                entryClass = Class.forName(split[0]);
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
    }
}
