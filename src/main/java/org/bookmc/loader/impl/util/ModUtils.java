package org.bookmc.loader.impl.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.Loader;

import java.util.ArrayList;
import java.util.List;

public class ModUtils {
    private final static Logger LOGGER = LogManager.getLogger(ModUtils.class);

    public static void resolveMods() {
        LOGGER.info("Beginning resolution of mods");
        int oldSize = Loader.getCandidates().size();

        for (ModResolver resolver : Loader.getModResolvers()) {
            String name = resolver.getClass().getSimpleName();
            LOGGER.info("Trying {} for any mods", name);
            resolver.resolve();
            int newSize = Loader.getCandidates().size();
            if (oldSize < newSize) {
                LOGGER.info("{} found {} new mods", name, newSize - oldSize);
            }
            oldSize = newSize;
        }
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
                        LOGGER.info("Accepted candidate: {}", vessel.getId());
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

        ClassloaderOperationUtils.sortClassLoaders(Loader.getModVessels());
        for (ModCandidate candidate : Loader.getCandidates()) {
            for (ModVessel vessel : candidate.getVessels()) {
                candidate.addToClasspath(vessel.getClassLoader());
            }
        }
    }
}
