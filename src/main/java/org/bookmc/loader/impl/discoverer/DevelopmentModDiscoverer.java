package org.bookmc.loader.impl.discoverer;

import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.LocalClassLoaderModCandidate;
import org.bookmc.loader.utils.ClassUtils;

import java.io.File;

public class DevelopmentModDiscoverer implements MinecraftModDiscoverer {
    @Override
    public void discover(File[] files) {
        if (ClassUtils.isClassAvailable("net.minecraft.client.Minecraft")) {
            Loader.registerCandidate(new LocalClassLoaderModCandidate());
        }
    }

    @Override
    public boolean isFilesRequired() {
        return false;
    }
}
