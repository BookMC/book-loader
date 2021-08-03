package org.bookmc.loader.impl.resolve;

import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.LocalClassLoaderModCandidate;
import org.bookmc.loader.shared.utils.ClassUtils;

import java.io.File;

public class DevelopmentModResolver implements ModResolver {
    @Override
    public void resolve(File[] files) {
        if (ClassUtils.isClassAvailable("net.minecraft.client.Minecraft")) {
            Loader.registerCandidate(new LocalClassLoaderModCandidate());
        }
    }
}
