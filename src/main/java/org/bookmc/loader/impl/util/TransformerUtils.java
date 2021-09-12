package org.bookmc.loader.impl.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.transformer.QuiltRemapper;
import org.bookmc.loader.api.transformer.QuiltTransformer;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.launch.BookLauncher;

import java.lang.reflect.InvocationTargetException;

public class TransformerUtils {
    private static final Logger LOGGER = LogManager.getLogger(TransformerUtils.class);

    public static void loadRemapper(ModVessel vessel, String remapper) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ClassLoader classLoader = vessel.getClassLoader();
        BookLauncher.getQuiltClassLoader().addClassLoaderExclusion(remapper);

        Class<?> clazz = Class.forName(remapper, false, classLoader);
        try {
            BookLauncher.getQuiltClassLoader()
                .registerRemapper((QuiltRemapper) clazz.getConstructor().newInstance());
        } catch (ClassCastException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error("{} defined a remapper ({}) but does not implement QuiltReampper", vessel.getId(), remapper, e);
        }
    }

    public static void loadTransformer(ModVessel vessel, String transformer) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ClassLoader classLoader = vessel.getClassLoader();
        BookLauncher.getQuiltClassLoader().addClassLoaderExclusion(transformer);
        Class<?> clazz = Class.forName(transformer, false, classLoader);

        try {
            BookLauncher.getQuiltClassLoader().registerTransformer((QuiltTransformer) clazz.getConstructor().newInstance());
        } catch (ClassCastException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error("{} defined a transformer ({}) but does not implement QuiltTransformer", vessel.getId(), transformer, e);
        }
    }
}
