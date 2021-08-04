package org.bookmc.loader.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.api.adapter.BookLanguageAdapter;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.classloader.ModClassLoader;
import org.bookmc.loader.api.compat.CompatiblityLayer;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.launch.transform.QuiltClassLoader;
import org.bookmc.loader.impl.resolve.BookModResolver;
import org.bookmc.loader.impl.resolve.ClasspathModResolver;
import org.bookmc.loader.impl.resolve.DevelopmentModResolver;
import org.bookmc.loader.impl.ui.MissingDependencyUI;
import org.bookmc.loader.shared.utils.DownloadUtils;
import org.bookmc.loader.shared.utils.ZipUtils;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Loader {
    public static final List<ModVessel> loaded = new ArrayList<>();
    private static final List<ModResolver> resolvers = new ArrayList<>();
    private static final Map<String, ModVessel> modVessels = new HashMap<>();
    private static final List<ModCandidate> candidates = new ArrayList<>();
    private static final List<ModCandidate> rejectedCandidates = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, ArrayList<String>> missingDependencies = new HashMap<>();

    private static final Object object = new Object();

    private static Environment environment = Environment.UNKNOWN;

    static {
        // Register default mod loader.
        Loader.registerResolver(new BookModResolver());
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
        return Collections.unmodifiableList(new ArrayList<>(modVessels.values()));
    }

    public static Map<String, ModVessel> getModVesselsMap() {
        return Collections.unmodifiableMap(modVessels);
    }

    public static void discover(File modsDirectory) {
        for (ModResolver discoverer : Loader.getModResolvers()) {
            File[] files = modsDirectory.listFiles();

            if (files == null) {
                files = new File[0];
            }

            discoverer.resolve(files);
        }
    }

    /**
     * Registers a ModCandidate onto the mod candidate list. If it has passed the initial process time you can reinvoke
     * {@link Loader#loadCandidates()}
     *
     * @param candidate The ModCandidate to be registered
     */
    public static void registerCandidate(ModCandidate candidate) {
        candidates.add(candidate);

    }

    /**
     * Returns the currently available candidates. If {@link Loader#loadCandidates()}
     * has been invoekd then it will only return accepted candidates however if this had not been invoked it
     * will contain rejected candidates so especially you CompatibilityLayer people beware!
     * Don't worry we've made it as safe as possible for developers to call our internals, interesting right?
     * If another [compatibility] layer calls {@link Loader#loadCandidates()} then
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

    public static void loadCompatibilityLayers(QuiltClassLoader classLoader) {
        for (ModVessel vessel : getModVessels()) {
            if (!isModLoaded(vessel.getId())) {
                loadCompatibilityLayer(vessel, classLoader);
            }
        }
    }

    public static void loadCompatibilityLayer(ModVessel vessel, IQuiltClassLoader classLoader) {
        Entrypoint[] entrypoints = vessel.getEntrypoints();
        for (Entrypoint entrypoint : entrypoints) {
            try {
                Class<?> compatClass = classLoader.getClassLoader()
                    .loadClass(CompatiblityLayer.class.getName());
                Class<?> clazz = Class.forName(entrypoint.getOwner(), false, classLoader.getClassLoader());

                if (clazz.isAssignableFrom(compatClass)) {
                    if (vessel.getDependsOn().length != 0) {
                        throw new IllegalDependencyException(vessel);
                    }

                    loaded.add(vessel); // Trick BookModLoader#load to believe we have "loaded" our "mod".
                    CompatiblityLayer layer = (CompatiblityLayer) clazz.newInstance();
                    layer.init(classLoader);
                }
            } catch (ClassCastException e) {
                throw new IllegalStateException("The entrypoint (" + entrypoint + ") does not implement CompatibilityLayer");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void loadMixins(Environment environment) {
        for (ModVessel vessel : Loader.getModVessels()) {
            loadMixin(vessel, environment);
        }
    }

    public static void loadMixin(ModVessel vessel, Environment environment) {
        MixinEntrypoint[] mixinEntrypoints = vessel.getMixinEntrypoints();

        for (MixinEntrypoint entrypoint : mixinEntrypoints) {
            if (environment.allows(entrypoint.getEnvironment())) {
                Mixins.addConfiguration(entrypoint.getMixinFile());
            }
        }
    }


    public static void loadTransformers(List<ModVessel> vessels) {
        for (ModVessel vessel : vessels) {
            try {
                loadTransformer(vessel);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadTransformer(ModVessel vessel) throws ClassNotFoundException {
        for (String transformer : vessel.getTransformers()) {
            Class<?> clazz = vessel.getAbstractedClassLoader()
                .getClassLoader()
                .loadClass(transformer);
            Class<? extends QuiltTransformer> transformerClass = clazz.asSubclass(QuiltTransformer.class);
            Launcher.getQuiltClassLoader().registerTransformer(transformerClass);
        }
    }

    public static void discoverAndLoad(File modsDirectory, Environment environment) throws
        IllegalDependencyException {
        Loader.discover(modsDirectory);
        loadCandidates();

        for (ModVessel vessel : Loader.getModVessels()) {
            Loader.loadCompatibilityLayer(vessel, vessel.getAbstractedClassLoader());
        }
    }

    public static void load(Environment environment) {
        Loader.environment = environment;

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
                load(vessel, vessel.getAbstractedClassLoader().getClassLoader(), environment);
            }
        }
    }

    private static void loadDependencies(ModVessel vessel, Environment environment) {
        ArrayList<String> missingDeps = missingDependencies.getOrDefault(vessel.getId(), new ArrayList<>());

        for (URL url : vessel.getExternalDependencies()) {
            File file = DownloadUtils.downloadFile(url, new File(Launcher.getGameProvider().getGameDirectory(), "libraries/" + url.getPath()));
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
                missingDeps.add("The dependency " + dependency.getId() + " was missing! Please make sure it's installed");
                continue;
            }

            // TODO: Replace with semver checking and >=/<=/>/< support
            if (!dependency.getVersion().equals("*") && !dependencyVessel.getVersion().equals(vessel.getVersion())) {
                missingDeps.add("The dependency " + dependency.getId() + " was located! However " + vessel.getName() + " requires " + dependency.getVersion() + " but " + dependencyVessel.getVersion() + " was given");
                continue;
            }


            loadDependencies(dependencyVessel, environment);
            if (!loaded.contains(dependencyVessel)) {
                load(dependencyVessel, dependencyVessel.getAbstractedClassLoader().getClassLoader(), environment);
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
                load(suggestionVessel, suggestionVessel.getAbstractedClassLoader().getClassLoader(), environment);
            }
        }

        if (!missingDependencies.isEmpty()) {
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
                    logger.debug("Loading " + vessel.getName() + " from " + entrypoint.getOwner());

                    Class<?> adapter = classLoader.loadClass(vessel.getLanguageAdapter());

                    // We do this to double check it actually implements what BookLanguageAdapter
                    adapter.asSubclass(classLoader.loadClass(BookLanguageAdapter.class.getName()));

                    BookLanguageAdapter adapterInstance = (BookLanguageAdapter) adapter.newInstance();


                    entryClass.getDeclaredMethod(entrypoint.getMethod())
                        .invoke(adapterInstance.createInstance(entryClass));
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                throw new IllegalStateException("The given language adpater does not implement BookLanguageAdapter", e);
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
                candidate.addToClasspath(vessel.getAbstractedClassLoader());
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

    /**
     * This is the main system to sort dependencies into different classloaders
     * It recursively calls the method {@link Loader#sortClassLoader(ModVessel)}
     * to check if it has any dependencies and if it does add the dependencies and itself to the classpath
     * if not stay on it's own classpath.
     * <p>
     * This was quite mentally exhausting to plan out how to make :)
     *
     * @param vessels The vessels to have their classloaders sorted.
     */
    public static void sortClassLoaders(List<ModVessel> vessels) {
        for (ModVessel vessel : vessels) {
            sortClassLoader(vessel);
        }
    }

    private static void sortClassLoader(ModVessel vessel) {
        if (vessel.getAbstractedClassLoader() == null) {
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

            if (dependencyVessel.getAbstractedClassLoader() == null) {
                sortClassLoader(dependencyVessel);
            }

            if (dependencyVessel.getAbstractedClassLoader() != vessel.getAbstractedClassLoader() && !dependencyVessel.isInternal()) {
                URL[] urls = dependencyVessel.getAbstractedClassLoader().getClassLoader().getURLs();
                for (URL url : urls) {
                    vessel.getAbstractedClassLoader().addURL(url);
                }
                dependencyVessel.setClassLoader(vessel.getAbstractedClassLoader());
                for (ModDependency modDependency : dependencyVessel.getDependsOn()) {
                    sortClassLoaderDependsOn(modDependency, dependencyVessel);
                }
            }
        }
    }

    private static void sortClassLoaderSuggests(ModDependency dependency, ModVessel vessel) {
        if (Loader.getModVesselsMap().containsKey(dependency.getId())) {
            ModVessel dependencyVessel = Loader.getModVesselsMap().get(dependency.getId());
            if (dependencyVessel.getAbstractedClassLoader() != vessel.getAbstractedClassLoader()) {
                URL[] urls = dependencyVessel.getAbstractedClassLoader().getClassLoader().getURLs();
                for (URL url : urls) {
                    vessel.getAbstractedClassLoader().addURL(url);
                }
                dependencyVessel.setClassLoader(vessel.getAbstractedClassLoader());
                for (ModDependency modDependency : dependencyVessel.getSuggestions()) {
                    sortClassLoaderSuggests(modDependency, dependencyVessel);
                }
            }
        }
    }
}
