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
            configDir = new File(getGameProvider().getGameDirectory(), "config");
        }

        return configDir;
    }

    public static GameProvider getGameProvider() {
        if (provider == null) {
            throw new IllegalStateException("The game has not been launched correctly! (No providers are available)");
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
            modsFolder = new File(getGameProvider().getGameDirectory(), System.getProperty("book.discovery.folder", "mods"));
        }

        return modsFolder;
    }

    /**
     * This method uses all our available classloaders to announce the method to.
     * We first use the parent ({@link QuiltClassLoader}) to check for the resource.
     * If it's not available we then iterate through our vessels and ask them for the
     * resource and if they return the resource when use that.
     * @param name The name of the resource to look for.
     * @return The looked up resource.
     */
    public static InputStream getResourceAsStream(String name) {
        // QuiltClassLoader should always be priority
        InputStream stream = Quilt.classLoader.getResourceAsStream(name);

        if (stream != null) {
            // If resolved use the resolved resource
            return stream;
        }

        // Move to the vessels and try find the resource from there
        for (ModVessel vessel : Loader.getModVessels()) {
            InputStream vesselResolved = vessel.getAbstractedClassLoader()
                .getClassLoader()
                .getResourceAsStream(name);
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

    /**
     * This check simply checks if two obfuscated classes don't exist.
     * If they were to exist then we are simply not in the development
     * environment because they would be deobfuscated in the development
     * environment.
     * @return Whether we are in the development environment or not
     */
    public static boolean isDevelopment() {
        // ave = Minecraft
        // ko = DedicatedServer
        return !ClassUtils.isClassAvailable("ave") || !ClassUtils.isClassAvailable("ko");
    }

    /**
     * This method uses all our available classloaders to search for class bytes via {@link IQuiltClassLoader}.
     * We first use the parent {@link QuiltClassLoader} and search for the class. If it isn't available there
     * when then move onto our mod vessels and we iterate through them to try find the resource
     * through their classloaders.
     * @param name The name of the class we want to locate
     * @param transform Whether we want the transformed version or not of the class
     * @return The bytes of the discovered class.
     */
    public static byte[] getClassBytes(String name, boolean transform) {
        IQuiltClassLoader classLoader = getQuiltClassLoader();
        byte[] classBytes = classLoader.getClassBytes(name, transform);

        if (classBytes != null) {
            return classBytes;
        }

        for (ModVessel vessel : Loader.getModVessels()) {
            IQuiltClassLoader loader = vessel.getAbstractedClassLoader();
            // Mod classloaders are currently unable to provide transformed classes
            // therefore they should never in reality be returning transformed classes
            // If we ask for a transformed class it will throw an exception so we must
            // purposely disallow usage of this.
            byte[] vesselResolved = loader.getClassBytes(name, false);
            if (vesselResolved != null) {
                // If we have resolved the resoucrce return it back.
                return vesselResolved;
            }
        }

        return null; // Mission imppossible
    }
}
