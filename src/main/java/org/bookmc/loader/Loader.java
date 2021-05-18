package org.bookmc.loader;

import org.bookmc.loader.book.BookModDiscoverer;
import org.bookmc.loader.book.DevelopmentModDiscoverer;
import org.bookmc.loader.vessel.ModVessel;
import org.bookmc.loader.vessel.json.library.LibraryModVessel;

import java.util.*;

public class Loader {
    private static final List<MinecraftModDiscoverer> discoverers = new ArrayList<>();
    private static final Map<String, ModVessel> modVessels = new HashMap<>();

    static {
        // Register default mod loader.
        Loader.registerModDiscoverer(new BookModDiscoverer());
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

    public static List<ModVessel> getLibrariesVessels() {
        ArrayList<ModVessel> vessels = new ArrayList<>();

        for (ModVessel vessel : modVessels.values()) {
            if (vessel instanceof LibraryModVessel) {
                vessels.add(vessel);
            }
        }

        return Collections.unmodifiableList(vessels);
    }

    public static List<ModVessel> getModVessels() {
        return Collections.unmodifiableList(new ArrayList<>(modVessels.values()));
    }

    public static Map<String, ModVessel> getModVesselsMap() {
        return Collections.unmodifiableMap(modVessels);
    }
}
