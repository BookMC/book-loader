package org.bookmc.external.compat;

import org.bookmc.loader.api.classloader.IQuiltClassLoader;

public interface CompatiblityLayer {
    void init(IQuiltClassLoader classLoader);
}
