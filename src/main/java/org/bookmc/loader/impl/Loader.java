package org.bookmc.loader.impl;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.discoverer.BookModDiscoverer;
import org.bookmc.loader.impl.discoverer.ClasspathModDiscoverer;
import org.bookmc.loader.impl.discoverer.DevelopmentModDiscoverer;

import java.io.File;
import java.util.*;

public class Loader {
    private static final List<MinecraftModDiscoverer> discoverers = new ArrayList<>();
    private static final Map<String, ModVessel> modVessels = new HashMap<>();

    private static final List<ModCandidate> candidates = new ArrayList<>();
    private static final List<ModCandidate> rejectedCandidates = new ArrayList<>();

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
        for (MinecraftModDiscoverer discoverer : Loader.getModDiscoverers()) {
            File[] files = modsDirectory.listFiles();

            if (files != null || !discoverer.isFilesRequired()) {
                discoverer.discover(files);
            }
        }
    }

    /**
     * Registers a ModCandidate onto the mod candidate list. If it has passed the initial process time you can reinvoke
     * {@link BookModLoader#loadCandidates(LaunchClassLoader)}
     *
     * @param candidate The ModCandidate to be registered
     */
    public static void registerCandidate(ModCandidate candidate) {
        candidates.add(candidate);

    }

    /**
     * Returns the currently available candidates. If {@link BookModLoader#loadCandidates(LaunchClassLoader)}
     * has been invoekd then it will only return accepted candidates however if this had not been invoked it
     * will contain rejected candidates so especially you CompatibilityLayer people beware!
     * Don't worry we've made it as safe as possible for developers to call our internals, interesting right?
     * If another [compatibility] layer calls {@link BookModLoader#loadCandidates(LaunchClassLoader)} then
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
}
