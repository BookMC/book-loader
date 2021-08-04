package org.bookmc.loader.api.compat;

import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;
import org.bookmc.loader.api.classloader.IQuiltClassLoader;

public interface CompatiblityLayer {
    void init(IQuiltClassLoader classLoader);
}
