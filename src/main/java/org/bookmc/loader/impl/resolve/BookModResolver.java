package org.bookmc.loader.impl.resolve;

import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.shared.Constants;

import java.io.File;

public class BookModResolver implements ModResolver {
    @Override
    public void resolve(File[] files) {
        if (files.length > 0) {
            for (File file : files) {
                if (!file.getName().endsWith(Constants.DISABLED_SUFFIX)) {
                    Loader.registerCandidate(new ZipModCandidate(file));
                }
            }
        }
    }
}
