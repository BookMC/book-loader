package org.bookmc.loader.impl.discoverer;

import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.ZipModCandidate;

import java.io.File;

public class BookModDiscoverer implements MinecraftModDiscoverer {
    private static final String DISABLED_SUFFIX = ".disabled";

    @Override
    public void discover(File[] files) {
        if (files.length > 0) {
            for (File file : files) {
                if (!file.getName().endsWith(DISABLED_SUFFIX)) {
                    Loader.registerCandidate(new ZipModCandidate(file));
                }
            }
        }
    }
}
