package org.bookmc.loader.impl.resolve;

import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.LocalClassLoaderModCandidate;

public class DevelopmentModResolver implements ModResolver {
    @Override
    public void resolve() {
        Loader.registerCandidate(new LocalClassLoaderModCandidate());
    }
}
