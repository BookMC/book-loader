package org.bookmc.loader.api.compat;

import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;

public interface CompatiblityLayer {
    void init(ClassLoaderURLAppender classLoader);
}
