package org.bookmc.loader.api.mod;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;

public interface ModCandidate {
    boolean validate();
    void loadContainers(AbstractBookURLClassLoader classLoader);

    ModContainer[] getContainers();
}
