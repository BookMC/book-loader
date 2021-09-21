package org.bookmc.loader.api.mod;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;

public interface ModCandidate {
    boolean validate();

    void loadContainers0(AbstractBookURLClassLoader classLoader);

    default void loadContainers(AbstractBookURLClassLoader classLoader) {
        loadContainers0(classLoader);
        for (ModContainer container : getContainers()) {
            container.setClassLoader(classLoader);
        }
    }

    ModContainer[] getContainers();
}
