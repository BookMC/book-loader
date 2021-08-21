package org.bookmc.loader.impl.launch;

import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.launch.provider.GameProvider;
import org.bookmc.loader.impl.launch.transform.QuiltClassLoader;
import org.bookmc.loader.impl.launch.transform.mixin.QuiltMixinProxyManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Launcher {
    private static final Map<String, Object> properties = new HashMap<>();
    private static Environment environment;
    private static File modsFolder;
    private static File configDir;
    private static GameProvider provider;
    private static QuiltMixinProxyManager proxyManager;

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
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        Launcher.environment = environment;
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
     *
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
                // If we have resolved the resource return it back.
                return vesselResolved;
            }
        }

        return null;
    }

    public static Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * This check simply checks if GradleStart exists
     * GradleStart comes from ForgeGradle as it adds
     * arguments and then launches the "bounce class"
     * (Quilt).
     *
     * @return Whether we are in the development environment or not
     */
    public static boolean isDevelopment() {
        return System.getenv("PG_MAIN_CLASS") != null;
    }

    /**
     * This method uses all our available classloaders to search for class bytes via {@link IQuiltClassLoader}.
     * We first use the parent {@link QuiltClassLoader} and search for the class. If it isn't available there
     * when then move onto our mod vessels and we iterate through them to try find the resource
     * through their classloaders.
     *
     * @param name      The name of the class we want to locate
     * @param transform Whether we want the transformed version or not of the class
     * @return The bytes of the discovered class.
     */
    public static byte[] getClassBytes(String name, boolean transform) {
        IQuiltClassLoader classLoader = getQuiltClassLoader();
        byte[] classBytes = classLoader.getClassBytes(name, transform);

        if (classBytes == null) {
            for (ModVessel vessel : Loader.getModVessels()) {
                IQuiltClassLoader loader = vessel.getAbstractedClassLoader();
                // Mod classloaders are currently unable to provide transformed classes
                // therefore they should never in reality be returning transformed classes
                // If we ask for a transformed class it will throw an exception so we must
                // purposely disallow usage of this.
                byte[] vesselResolved = loader.getClassBytes(name, transform);
                if (vesselResolved != null) {
                    // If we have resolved the resource return it back.
                    classBytes = vesselResolved;
                    break;
                }
            }
        }

        return classBytes;// Mission impossible
    }

    /**
     * Resolves a ClassNode
     *
     * @param name      The name of the class to find
     * @param transform Whether the ClassNode should be transformed
     * @param flags     The flags to provide to the ClassReader
     * @return The resolved ClassNode as an ASM ClassNode
     */
    public static ClassNode getClassNode(String name, boolean transform, int flags) {
        byte[] clazz = getClassBytes(name, transform);

        if (clazz == null) {
            return null;
        }

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(clazz);
        reader.accept(node, flags);
        return node;
    }

    /**
     * Resolves a ClassNode
     *
     * @param name      The name of the class to find
     * @param transform Whether the ClassNode should be transformed
     * @return The resolved ClassNode as an ASM ClassNode
     */
    public static ClassNode getClassNode(String name, boolean transform) {
        return getClassNode(name, transform, ClassReader.EXPAND_FRAMES);
    }

    public static boolean isClassLoaded(String name) {
        QuiltClassLoader classLoader = getQuiltClassLoader();
        boolean loaded = classLoader.isClassLoaded(name);

        // If we know the class is loaded on the parent classloader
        // lets use it! However if it isn't then instead let's give
        // the little guys (the ModClassLoaders) search for it.
        if (loaded) {
            return true;
        }

        for (ModVessel vessel : Loader.getModVessels()) {
            // If the mod finds the class then let's send the good news
            if (vessel.getAbstractedClassLoader().isClassLoaded(name)) {
                return true;
            }
        }

        // Give up on the operation
        return false;
    }

    /**
     * This function grabs all the available URLs to Quilt. These include
     * the URLs on the main classloader {@link QuiltClassLoader} and on
     * classloaders such as the {@link org.bookmc.loader.api.classloader.ModClassLoader}s.
     *
     * @return All available URLs
     */
    public static URL[] getURLs() {
        List<URL> urls = new ArrayList<>(Arrays.asList(getQuiltClassLoader().getURLs()));

        for (ModVessel vessel : Loader.getModVessels()) {
            urls.addAll(Arrays.asList(vessel.getAbstractedClassLoader().getClassLoader().getURLs()));
        }

        return urls.toArray(new URL[0]);
    }

    /**
     * This method is quite a messy function but goes through each vessel
     * and attempts to load a class until it succeeds. If the class is never
     * found we just give up and throw an exception instead.
     *
     * @param name               The name of the class we are trying to load.
     * @param tryMainClassLoader Whether we should also include the main classloader in the hunt.
     * @return The discovered class.
     * @throws ClassNotFoundException We could not locate the class anywhere!
     */
    public static Class<?> loadClass(String name, boolean tryMainClassLoader) throws ClassNotFoundException {
        if (tryMainClassLoader) {
            QuiltClassLoader mainClassLoader = getQuiltClassLoader();
            if (mainClassLoader.isClassAvailable(name)) {
                return mainClassLoader.loadClass(name);
            }
        }

        for (ModVessel vessel : Loader.getModVessels()) {
            try {
                URLClassLoader classLoader = vessel.getAbstractedClassLoader()
                    .getClassLoader();

                // Avoid a StackOverflow
                if (classLoader instanceof QuiltClassLoader) continue;

                if (vessel.getAbstractedClassLoader().isClassAvailable(name)) {
                    return classLoader.loadClass(name);
                }
            } catch (Throwable ignored) {
                // Failed to find the class from this classloader.
                // Deciding to continue and check the others...
            }
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * Searches through all the classloaders available to us and tries to find a class from it.
     *
     * @param name       The class we are trying to find
     * @param initialize Whether we should initialize the located class
     * @return The located class
     * @throws ClassNotFoundException The class has not been located
     */
    public static Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        try {
            return Class.forName(name, initialize, getQuiltClassLoader());
        } catch (ClassNotFoundException e) {
            for (ModVessel vessel : Loader.getModVessels()) {
                try {
                    return Class.forName(name, initialize, vessel.getAbstractedClassLoader().getClassLoader());
                } catch (ClassNotFoundException ignored) {

                }
            }
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * !! FOR DEVELOPMENT USE ONLY !!
     * <p>
     * Grabs the GradleStart property given to use at launch via ForgeGradle and gives
     * us an exact location of where the mappings are!
     *
     * @return A file instance of the Notch to MCP mappings.
     */
    public static File getMappings() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static QuiltMixinProxyManager getProxyManager() {
        return proxyManager;
    }
}
