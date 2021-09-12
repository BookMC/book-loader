package org.bookmc.external.compat;

import org.bookmc.loader.api.classloader.AbstractBookClassLoader;

public interface CompatiblityLayer {
    void init(AbstractBookClassLoader classLoader);
}
