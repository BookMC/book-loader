package org.bookmc.loader.impl.resolve;

import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.shared.Constants;

import java.io.File;

public record BookModResolver(File directory) implements ModResolver {
    @Override
    public void resolve() {
        File[] candidates = directory.listFiles();

        if (candidates != null) {
            for (File file : candidates) {
                if (!file.getName().endsWith(Constants.DISABLED_SUFFIX)) {
                    Loader.registerCandidate(new ZipModCandidate(file));
                }
            }
        }
    }
}
