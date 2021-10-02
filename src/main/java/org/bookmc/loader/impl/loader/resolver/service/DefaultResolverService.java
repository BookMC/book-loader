package org.bookmc.loader.impl.loader.resolver.service;

import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.api.mod.resolution.ResolverService;
import org.bookmc.loader.impl.loader.resolver.ClasspathModResolver;
import org.bookmc.loader.impl.loader.resolver.FakeContainerResolver;
import org.bookmc.loader.impl.loader.resolver.FolderModResolver;

public class DefaultResolverService implements ResolverService {
    @Override
    public ModResolver[] getModResolvers() {
        return new ModResolver[]{
            new FolderModResolver(BookLoaderBase.INSTANCE.getModsPath()),
            new ClasspathModResolver(),
            new FakeContainerResolver()
        };
    }
}
