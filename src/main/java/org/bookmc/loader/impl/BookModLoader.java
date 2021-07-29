package org.bookmc.loader.impl;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
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
                load(vessel, Launch.classLoader);
            }
        }
    }

    private static void loadDependencies(ModVessel vessel) {
        ArrayList<String> missingDependencies = BookModLoader.missingDependencies.getOrDefault(vessel.getId(), new ArrayList<>());

        for (ModDependency dependency : vessel.getDependsOn()) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());

            if (dependencyVessel == null) {
                missingDependencies.add("The dependency " + dependency.getId() + " was missing! Please make sure it's installed");
                continue;
            }

            // TOOD: Replace with semver checking and >=/<=/>/< support
            if (!dependency.getVersion().equals("*") && !dependencyVessel.getVersion().equals(vessel.getVersion())) {
                missingDependencies.add("The dependency " + dependency.getId() + " was located! However " + vessel.getName() + " requires " + dependency.getVersion());
                continue;
            }


            loadDependencies(dependencyVessel);
            load(dependencyVessel, Launch.classLoader);
        }

        for (ModDependency dependency : vessel.getSuggestions()) {
            ModVessel suggestionVessel = Loader.getModVesselsMap().get(dependency.getId());

            if (suggestionVessel == null) {
                logger.info("The mod " + vessel.getName() + " suggests that you should install " + dependency.getId() + ".");
                continue;
            }

            if (!dependency.getVersion().equals("*") && !suggestionVessel.getVersion().equals(vessel.getVersion())) {
                logger.info("The dependency " + dependency.getId() + " was located! However " + vessel.getName() + " wants " + dependency.getVersion());
                continue;
            }

            loadDependencies(suggestionVessel);
            load(suggestionVessel, Launch.classLoader);
        }

        if (!missingDependencies.isEmpty()) {
            BookModLoader.missingDependencies.put(vessel.getId(), missingDependencies);
        }
    }

    private static void load(ModVessel vessel, ClassLoader classLoader) {
        if (!vessel.isInternallyEnabled() || vessel.isCompatibilityLayer()) return;
        String[] split = vessel.getEntrypoint().split("::");

        Class<?> entryClass = null;

        try {
            entryClass = Class.forName(split[0], false, classLoader);
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

    public static void loadCandidates(LaunchClassLoader classLoader) {
        // To avoid a CMFE we have to add the to-be-removed candidates
        // to a new list and then iterate over it and remove the rejects
        // Once we're finished processing.
        List<ModCandidate> removeQueue = new ArrayList<>();

        for (ModCandidate candidate : Loader.getCandidates()) {
            if (candidate.isAcceptable()) {
                for (ModVessel vessel : candidate.getVessels()) {
                    if (!Loader.isVesselDiscovered(vessel.getId())) {
                        candidate.addToClasspath(classLoader);
                        Loader.registerVessel(vessel);
                    } else {
                        removeQueue.add(candidate);
                    }
                }
            } else {
                removeQueue.add(candidate);
                if (!Loader.getRejectedCandidates().contains(candidate)) {
                    Loader.rejectCandidate(candidate);
                }
            }
        }

        for (ModCandidate candidate : removeQueue) {
            Loader.getCandidates().remove(candidate);
        }
    }

    public static boolean isModLoaded(String id) {
        for (ModVessel vessel : loaded) {
            if (vessel.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }
}
