package org.bookmc.loader.impl;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;
import org.bookmc.loader.api.classloader.ModClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.impl.ui.MissingDependencyUI;
import org.bookmc.loader.shared.utils.DownloadUtils;
import org.bookmc.loader.shared.utils.ZipUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookLauncherBase {
    public static final List<ModVessel> loaded = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, ArrayList<String>> missingDependencies = new HashMap<>();

    private static final Object object = new Object();

    private static Environment environment = Environment.UNKNOWN;

    public static void load(Environment environment) {
        BookLauncherBase.environment = environment;

        for (ModVessel vessel : Loader.getModVessels()) {
            if (loaded.contains(vessel)) {
                continue;
            }

            loadDependencies(vessel, environment);
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
                load(vessel, vessel.getClassLoader(), environment);
            }
        }
    }

    private static void loadDependencies(ModVessel vessel, Environment environment) {
        ArrayList<String> missingDependencies = BookLauncherBase.missingDependencies.getOrDefault(vessel.getId(), new ArrayList<>());

        for (URL url : vessel.getExternalDependencies()) {
            File file = DownloadUtils.downloadFile(url, new File(Launch.minecraftHome, "libraries/" + url.getPath()));
            logger.info("Downloaded an external dependency (" + file.getName() + ") from " + vessel.getName() + ".");

            if (ZipUtils.isZipFile(file)) {
                Loader.registerCandidate(new ZipModCandidate(file));
            } else {
                logger.error("The external library (" + file.getName() + ") is not a jar/zip! Ignoring and deleteing...");
                if (!file.delete()) {
                    logger.fatal("Failed to delete external library!");
                }
            }
        }

        loadCandidates(); // Reload our candidates since we added new stuff

        for (ModDependency dependency : vessel.getDependsOn()) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());

            if (dependencyVessel == null) {
                missingDependencies.add("The dependency " + dependency.getId() + " was missing! Please make sure it's installed");
                continue;
            }

            // TODO: Replace with semver checking and >=/<=/>/< support
            if (!dependency.getVersion().equals("*") && !dependencyVessel.getVersion().equals(vessel.getVersion())) {
                missingDependencies.add("The dependency " + dependency.getId() + " was located! However " + vessel.getName() + " requires " + dependency.getVersion() + " but " + dependencyVessel.getVersion() + " was given");
                continue;
            }


            loadDependencies(dependencyVessel, environment);
            if (!loaded.contains(dependencyVessel)) {
                load(dependencyVessel, dependencyVessel.getClassLoader(), environment);
            }
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

            loadDependencies(suggestionVessel, environment);
            if (!loaded.contains(suggestionVessel)) {
                load(suggestionVessel, suggestionVessel.getClassLoader(), environment);
            }
        }

        if (!missingDependencies.isEmpty()) {
            BookLauncherBase.missingDependencies.put(vessel.getId(), missingDependencies);
        }
    }

    private static void load(ModVessel vessel, ClassLoader classLoader, Environment environment) {
        if (vessel.getEntrypoints().length <= 0 || !environment.allows(vessel.getEnvironment())) return;
        Entrypoint[] entrypoints = vessel.getEntrypoints();

        for (Entrypoint entrypoint : entrypoints) {

            Class<?> entryClass = null;

            try {
                entryClass = Class.forName(entrypoint.getOwner(), false, classLoader);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            loaded.add(vessel);
            try {
                if (entryClass != null) {
                    logger.debug("Loading " + vessel.getName() + " from " + entrypoint.getOwner());
                    entryClass.getDeclaredMethod(entrypoint.getMethod()).invoke(entryClass.getConstructor().newInstance());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reload(File directory) {
        Loader.discover(directory);
        loadCandidates();

        load(environment);
    }

    public static void loadCandidates() {
        // To avoid a CMFE we have to add the to-be-removed candidates
        // to a new list and then iterate over it and remove the rejects
        // Once we're finished processing.
        List<ModCandidate> removeQueue = new ArrayList<>();

        for (ModCandidate candidate : Loader.getCandidates()) {
            if (candidate.isResolvable()) {
                for (ModVessel vessel : candidate.getVessels()) {
                    if (!Loader.isVesselDiscovered(vessel.getId())) {
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

        sortClassLoaders(Loader.getModVessels());
        for (ModCandidate candidate : Loader.getCandidates()) {
            for (ModVessel vessel : candidate.getVessels()) {
                candidate.addToClasspath(new ClassLoaderURLAppender(vessel.getClassLoader()));
            }
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

    public static URLClassLoader getTransformationClassLoader() {
        return Launch.classLoader;
    }

    public static void sortClassLoaders(List<ModVessel> vessels) {
        for (ModVessel vessel : vessels) {
            sortClassLoader(vessel);
        }
    }

    private static void sortClassLoader(ModVessel vessel) {
        if (vessel.getClassLoader() == null) {
            URL[] urls = new URL[0];
            if (vessel.getFile() != null) {
                try {
                    urls = new URL[]{vessel.getFile().toURI().toURL()};
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            vessel.setClassLoader(new ModClassLoader(urls));
        }
        for (ModDependency dependency : vessel.getDependsOn()) {
            sortClassLoaderDependsOn(dependency, vessel);
        }
        for (ModDependency suggestion : vessel.getSuggestions()) {
            sortClassLoaderSuggests(suggestion, vessel);
        }
    }

    private static void sortClassLoaderDependsOn(ModDependency dependency, ModVessel vessel) {
        if (Loader.getModVesselsMap().containsKey(dependency.getId())) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());

            if (dependencyVessel.getClassLoader() == null) {
                sortClassLoader(dependencyVessel);
            }

            if (dependencyVessel.getClassLoader() != vessel.getClassLoader() && !dependencyVessel.isInternal()) {
                URL[] urls = dependencyVessel.getClassLoader().getURLs();
                for (URL url : urls) {
                    ClassLoaderURLAppender.add(vessel.getClassLoader(), url);
                }
                dependencyVessel.setClassLoader(vessel.getClassLoader());
                for (ModDependency modDependency : dependencyVessel.getDependsOn()) {
                    sortClassLoaderDependsOn(modDependency, dependencyVessel);
                }
            }
        }
    }

    private static void sortClassLoaderSuggests(ModDependency dependency, ModVessel vessel) {
        if (Loader.getModVesselsMap().containsKey(dependency.getId())) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());
            if (dependencyVessel.getClassLoader() != vessel.getClassLoader()) {
                URL[] urls = dependencyVessel.getClassLoader().getURLs();
                for (URL url : urls) {
                    ClassLoaderURLAppender.add(vessel.getClassLoader(), url);
                }
                dependencyVessel.setClassLoader(vessel.getClassLoader());
                for (ModDependency modDependency : dependencyVessel.getSuggestions()) {
                    sortClassLoaderSuggests(modDependency, dependencyVessel);
                }
            }
        }
    }
}
