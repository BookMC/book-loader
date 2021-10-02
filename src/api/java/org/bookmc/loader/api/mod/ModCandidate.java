package org.bookmc.loader.api.mod;

import org.bookmc.loader.api.classloader.AppendableURLClassLoader;

public interface ModCandidate {
    boolean validate();

    void loadContainers0(AppendableURLClassLoader classLoader);

    default void loadContainers(AppendableURLClassLoader classLoader) {
        loadContainers0(classLoader);
        for (ModContainer container : getContainers()) {
            container.setClassLoader(classLoader);
        }
    }

    ModContainer[] getContainers();
}
