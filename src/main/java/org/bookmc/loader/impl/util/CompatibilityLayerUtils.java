package org.bookmc.loader.impl.util;

import org.bookmc.external.compat.CompatiblityLayer;
import org.bookmc.loader.api.classloader.AbstractBookClassLoader;
import org.bookmc.loader.api.exception.IllegalDependencyException;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.impl.Loader;

public class CompatibilityLayerUtils {
    public static void loadCompatibilityLayer(ModVessel vessel, AbstractBookClassLoader classLoader) {
        Class<?> compatClass = null;
        try {
            compatClass = classLoader.loadClass(CompatiblityLayer.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (compatClass != null) {
            Entrypoint[] entrypoints = vessel.getEntrypoints();
            for (Entrypoint entrypoint : entrypoints) {
                try {
                    Class<?> clazz = Class.forName(entrypoint.getOwner(), false, classLoader);

                    if (compatClass.isAssignableFrom(clazz)) {
                        if (vessel.getDependsOn().length != 0) {
                            throw new IllegalDependencyException(vessel);
                        }

                        Loader.loaded.add(vessel); // Trick BookModLoader#load to believe we have "loaded" our "mod".
                        Object layer = clazz.getConstructor().newInstance();
                        clazz.getDeclaredMethod("init", AbstractBookClassLoader.class)
                            .invoke(layer, classLoader);
                    }
                } catch (ClassCastException ignored) {

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
