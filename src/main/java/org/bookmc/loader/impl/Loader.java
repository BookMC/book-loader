package org.bookmc.loader.impl;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;
import org.bookmc.loader.api.compat.CompatiblityLayer;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.resolve.BookModResolver;
import org.bookmc.loader.impl.resolve.ClasspathModResolver;
import org.bookmc.loader.impl.resolve.DevelopmentModResolver;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.net.URLClassLoader;
import java.util.*;

public class Loader {
    private static final List<ModResolver> resolvers = new ArrayList<>();
    private static final Map<String, ModVessel> modVessels = new HashMap<>();

    private static final List<ModCandidate> candidates = new ArrayList<>();
    private static final List<ModCandidate> rejectedCandidates = new ArrayList<>();

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
     * {@link BookLauncherBase#loadCandidates()}
     *
     * @param candidate The ModCandidate to be registered
     */
    public static void registerCandidate(ModCandidate candidate) {
        candidates.add(candidate);

    }

    /**
     * Returns the currently available candidates. If {@link BookLauncherBase#loadCandidates()}
     * has been invoekd then it will only return accepted candidates however if this had not been invoked it
     * will contain rejected candidates so especially you CompatibilityLayer people beware!
     * Don't worry we've made it as safe as possible for developers to call our internals, interesting right?
     * If another [compatibility] layer calls {@link BookLauncherBase#loadCandidates()} then
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

    public static void loadCompatibilityLayers(LaunchClassLoader classLoader) {
        for (ModVessel vessel : getModVessels()) {
            if (!BookLauncherBase.isModLoaded(vessel.getId())) {
                loadCompatibilityLayer(vessel, classLoader);
            }
        }
    }

    public static void loadCompatibilityLayer(ModVessel vessel, URLClassLoader classLoader) {
        Entrypoint[] entrypoints = vessel.getEntrypoints();
        for (Entrypoint entrypoint : entrypoints) {
            try {
                Class<?> compatClass = classLoader.loadClass(CompatiblityLayer.class.getName());
                Class<?> clazz = Class.forName(entrypoint.getOwner(), false, classLoader);

                if (clazz.isAssignableFrom(compatClass)) {
                    if (vessel.getDependsOn().length != 0) {
                        throw new IllegalDependencyException(vessel);
                    }

                    BookLauncherBase.loaded.add(vessel); // Trick BookModLoader#load to believe we have "loaded" our "mod".
                    CompatiblityLayer layer = (CompatiblityLayer) clazz.newInstance();
                    layer.init(new ClassLoaderURLAppender(classLoader));
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

    public static void discoverAndLoad(File modsDirectory, Environment environment) throws
        IllegalDependencyException {
        Loader.discover(modsDirectory);
        BookLauncherBase.loadCandidates();

        for (ModVessel vessel : Loader.getModVessels()) {
            Loader.loadCompatibilityLayer(vessel, vessel.getClassLoader());
            Loader.loadMixin(vessel, environment);
        }
    }
}
