package org.bookmc.loader.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.ModResolver;
import org.bookmc.external.adapter.BookLanguageAdapter;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.impl.launch.BookLauncher;
import org.bookmc.loader.impl.mixin.BookMixinBootstrap;
import org.bookmc.loader.impl.resolve.ClasspathModResolver;
import org.bookmc.loader.impl.resolve.DevelopmentModResolver;
import org.bookmc.loader.impl.ui.MissingDependencyUI;
import org.bookmc.loader.impl.util.CompatibilityLayerUtils;
import org.bookmc.loader.impl.util.ModUtils;
import org.bookmc.loader.impl.util.TransformerUtils;
import org.bookmc.loader.shared.utils.DownloadUtils;
import org.bookmc.loader.shared.utils.VersionUtil;
import org.bookmc.loader.shared.utils.ZipUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

public class Loader {
    public static final List<ModVessel> loaded = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(Loader.class);
    private static final List<ModResolver> resolvers = new ArrayList<>();
    private static final Map<String, ModVessel> modVessels = new HashMap<>();
    private static final List<ModCandidate> candidates = new ArrayList<>();
    private static final List<ModCandidate> rejectedCandidates = new ArrayList<>();
    private static final Map<String, ArrayList<String>> missingDependencies = new HashMap<>();

    private static final Object object = new Object();

    static {
        // Register default resolvers
        Loader.registerResolver(new ClasspathModResolver());
        Loader.registerResolver(new DevelopmentModResolver());
    }

    public static void registerResolver(ModResolver modResolver) {
        resolvers.add(modResolver);
    }

    public static List<ModResolver> getModResolvers() {
        return Collections.unmodifiableList(resolvers);
    }

    public static void registerVessel(ModVessel vessel) {
        if (modVessels.containsKey(vessel.getId())) {
            throw new IllegalStateException("Two mods cannot have the same id!");
        }
        modVessels.put(vessel.getId(), vessel);
    }

    public static List<ModVessel> getModVessels() {
        return List.copyOf(modVessels.values());
    }

    public static Map<String, ModVessel> getModVesselsMap() {
        return Collections.unmodifiableMap(modVessels);
    }

    /**
     * Registers a ModCandidate onto the mod candidate list. If it has passed the initial process time you can reinvoke
     * {@link ModUtils#loadCandidates()}
     *
     * @param candidate The ModCandidate to be registered
     */
    public static void registerCandidate(ModCandidate candidate) {
        candidates.add(candidate);

    }

    /**
     * Returns the currently available candidates. If {@link ModUtils#loadCandidates()}
     * has been invoked then it will only return accepted candidates however if this had not been invoked it
     * will contain rejected candidates so especially you CompatibilityLayer people beware!
     * Don't worry we've made it as safe as possible for developers to call our internals, interesting right?
     * If another [compatibility] layer calls {@link ModUtils#loadCandidates()} then
     * it will simply skip the candidate if it has already been checked.
     *
     * @return Read note
     */
    public static List<ModCandidate> getCandidates() {
        return candidates;
    }

    /**
     * Returns the rejected candidates. See the documentation of {@link Loader#getCandidates()} for more details.
     *
     * @return Read the documentation!
     */
    public static List<ModCandidate> getRejectedCandidates() {
        return Collections.unmodifiableList(rejectedCandidates);
    }

    public static void rejectCandidate(ModCandidate candidate) {
        rejectedCandidates.add(candidate);
    }

    public static boolean isVesselDiscovered(String id) {
        for (ModVessel vessel : getModVessels()) {
            if (vessel.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public static void discoverAndLoad() throws IllegalDependencyException {
        ModUtils.resolveMods();
        ModUtils.loadCandidates();

        for (ModVessel vessel : getModVessels()) {
            CompatibilityLayerUtils.loadCompatibilityLayer(vessel, vessel.getClassLoader());

            for (String transformer : vessel.getTransformers()) {
                try {
                    TransformerUtils.loadTransformer(vessel, transformer);
                } catch (Exception e) {
                    LOGGER.error("Could not load transformer {}", transformer, e);
                }
            }

            for (String remapper : vessel.getRemappers()) {
                try {
                    TransformerUtils.loadRemapper(vessel, remapper);
                } catch (Exception e) {
                    LOGGER.error("Could not load remapper {}", remapper, e);
                }
            }

            BookMixinBootstrap.loadMixins(BookLauncher.getEnvironment());
        }
    }

    public static void load(Environment environment) {
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
                throw new RuntimeException("Something went wrong when attempting to block the thread!");
            }

            if (!loaded.contains(vessel)) {
                load(vessel, vessel.getClassLoader(), environment);
            }
        }
    }

    private static void loadDependencies(ModVessel vessel, Environment environment) {
            ArrayList<String> missingDeps = missingDependencies.getOrDefault(vessel.getId(), new ArrayList<>());

        boolean reload = false;

        for (URL url : vessel.getExternalDependencies()) {
            File file = DownloadUtils.downloadFile(url, new File(BookLauncher.getGameProvider().getGameDirectory(), ".book-libraries/" + url.getPath()));
            LOGGER.info("Downloaded an external dependency (" + file.getName() + ") from " + vessel.getName() + ".");

            if (ZipUtils.isZipFile(file)) {
                Loader.registerCandidate(new ZipModCandidate(file));
                reload = true;
            } else {
                LOGGER.error("The external library (" + file.getName() + ") is not a jar/zip! Ignoring and deleteing...");
                if (!file.delete()) {
                    LOGGER.fatal("Failed to delete external library!");
                }
            }
        }

        if (reload) {
            ModUtils.loadCandidates(); // Reload our candidates since we added new stuff
        }

        for (ModDependency dependency : vessel.getDependsOn()) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());

            if (dependencyVessel == null) {
                missingDeps.add("The dependency " + dependency.getId() + " was missing! Please make sure it's installed");
                continue;
            }

            String requiredVersion = dependency.getVersion();

            if (!dependency.getVersion().equals("*") && !VersionUtil.checkVersion(requiredVersion, dependencyVessel.getVersion())) {
                missingDeps.add("The dependency " + dependency.getId() + " was located! However " + vessel.getName() + " requires " + dependency.getVersion() + " but " + dependencyVessel.getVersion() + " was given");
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
                LOGGER.info("The mod {} suggests that you should install {}.", vessel.getName(), dependency.getId());
                continue;
            }

            if (!dependency.getVersion().equals("*") && !suggestionVessel.getVersion().equals(vessel.getVersion())) {
                LOGGER.info("The dependency " + dependency.getId() + " was located! However " + vessel.getName() + " wants " + dependency.getVersion());
                continue;
            }

            loadDependencies(suggestionVessel, environment);
            if (!loaded.contains(suggestionVessel)) {
                load(suggestionVessel, suggestionVessel.getClassLoader(), environment);
            }
        }

        if (!missingDeps.isEmpty()) {
            missingDependencies.put(vessel.getId(), missingDeps);
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
                    LOGGER.debug("Loading " + vessel.getName() + " from " + entrypoint.getOwner());

                    Class<?> adapter = classLoader.loadClass(vessel.getLanguageAdapter());

                    if (!adapter.isAssignableFrom(BookLanguageAdapter.class)) {
                        throw new ClassCastException();
                    }

                    Object adapterInstance = adapter.getConstructor().newInstance();

                    Object instance = adapter.getDeclaredMethod("createInstance", Class.class)
                        .invoke(adapterInstance, entryClass);

                    entryClass.getDeclaredMethod(entrypoint.getMethod())
                        .invoke(instance);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                throw new IllegalStateException("The given language adpater does not implement BookLanguageAdapter", e);
            }
        }
    }

    public static boolean isModLoaded(String id) {
        return modVessels.containsKey(id);
    }
}
