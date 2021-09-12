package org.bookmc.external.adapter;

import java.lang.reflect.InvocationTargetException;

/**
 * Language adapters are a concept where the loader instead of depending on itself
 * to correctly initialize classes of the {@link org.bookmc.loader.api.vessel.ModVessel}
 * asks the vessel how they would like it. If they were too rude to not respond (didn't specify
 * the language adapter) then we'll just use the {@link org.bookmc.external.adapter.java.JavaLanguageAdapter}
 *
 * @author ChachyDev
 */
public interface BookLanguageAdapter {
    /**
     * Asks the language adapter to use the given {@link Class} instance and
     * conjure an instance of that class out of it.
     *
     * @param clazz The class to initialize
     * @return The initialized class
     * @throws InstantiationException Failed to initialize the class
     * @throws IllegalAccessException The class is most likely not public and we tried to access it
     * @author ChachyDev
     */
    Object createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException;
}
