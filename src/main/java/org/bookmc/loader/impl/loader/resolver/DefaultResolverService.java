package org.bookmc.loader.impl.loader.resolver;

import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.api.mod.resolution.ResolverService;

public class DefaultResolverService implements ResolverService {
    @Override
    public ModResolver[] getModResolvers() {
        return new ModResolver[]{
            new FolderModResolver(BookLoaderBase.INSTANCE.getModsPath())
        };
    }
}
