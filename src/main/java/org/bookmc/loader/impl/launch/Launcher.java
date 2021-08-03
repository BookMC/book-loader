package org.bookmc.loader.impl.launch;

import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.launch.provider.GameProvider;
import org.bookmc.loader.impl.launch.transform.QuiltClassLoader;
import org.bookmc.loader.shared.utils.ClassUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Launcher {
    private static final Map<String, Object> properties = new HashMap<>();
    private static Environment environment;
    private static File modsFolder;
    private static File configDir;
    private static GameProvider provider;

    public static File getConfigDirectory() {
        if (configDir == null) {
            configDir = new File(getGameProvidier().getGameDirectory(), "config");
        }

        return configDir;
    }

    public static GameProvider getGameProvidier() {
        if (provider == null) {
            throw new IllegalStateException("The game has not been launched correctlye! (No providers are available)");
        }

        return provider;
    }

    public static void setGameProvider(GameProvider provider) {
        if (provider != null) {
            Launcher.provider = provider;
        }
    }


    public static QuiltClassLoader getQuiltClassLoader() {
        return Quilt.classLoader;
    }

    public static Environment getEnvironment() {
        if (environment == null) {
            environment = ClassUtils.isClassAvailable(System.getProperty("book.launcher.obfuscatedGameClass", "ave")) || ClassUtils.isClassAvailable("GradleStart") ? Environment.CLIENT : Environment.SERVER;
        }

        return environment;
    }

    public static File getModsFolder() {
        if (modsFolder == null) {
            modsFolder = new File(getGameProvidier().getGameDirectory(), System.getProperty("book.discovery.folder", "mods"));
        }

        return modsFolder;
    }

    public static InputStream getResourceAsStream(String name) {
        // QuiltClassLoader should always be priority
        InputStream stream = Quilt.classLoader.getResourceAsStream(name);

        if (stream != null) {
            // If resolved use the resolved resource
            return stream;
        }

        // Move to the vessels and try find the resource from there
        for (ModVessel vessel : Loader.getModVessels()) {
            InputStream vesselResolved = vessel.getAbstractedClassLoader().getClassLoader().getResourceAsStream(name);
            if (vesselResolved != null) {
                // If we have resolved the resoucrce return it back.
                return vesselResolved;
            }
        }

        return null;
    }

    public static Map<String, Object> getProperties() {
        return properties;
    }

    public static boolean isDevelopment() {
        return !ClassUtils.isClassAvailable("ave");
    }

    public static byte[] getClassBytes(String name, boolean transform) {
        IQuiltClassLoader classLoader = getQuiltClassLoader();
        byte[] classBytes = classLoader.getClassBytes(name, transform);

        if (classBytes != null) {
            return classBytes;
        }

        for (ModVessel vessel : Loader.getModVessels()) {
            byte[] vesselResolved = vessel.getAbstractedClassLoader().getClassBytes(name, transform);
            if (vesselResolved != null) {
                // If we have resolved the resoucrce return it back.
                return vesselResolved;
            }
        }

        return null; // Mission imppossible
    }
}
