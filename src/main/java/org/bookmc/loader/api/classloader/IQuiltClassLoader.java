package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.launch.transform.QuiltRemapper;
import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.launch.transform.mixin.QuiltMixinProxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public interface IQuiltClassLoader {
    byte[] getCachedClass(String name);

    void putCachedClass(String name, byte[] bytes);

    default byte[] getClassBytes(String name, boolean transform) {
        byte[] classBytes = getCachedClass(name);

        if (classBytes == null) {
            try {
                try (InputStream stream = getClassLoader().getResourceAsStream(name.replace(".", "/").concat(".class"))) {
                    if (stream != null) {
                        classBytes = stream.readAllBytes();
                        // Cached class data should always be clean...
                        putCachedClass(name, classBytes);
                    } else {
                        // No point in giving transformers/remappers null bytes just return.
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (QuiltRemapper remapper : Launcher.getQuiltClassLoader().getRemappers()) {
            classBytes = remapper.transform(name, classBytes);
        }

        if (transform) {
            for (QuiltTransformer transformer : Launcher.getQuiltClassLoader().getTransformers()) {
                classBytes = transformer.transform(name, classBytes);
            }
        }

        return classBytes;
    }

    default byte[] getClassBytesMixin(String name, boolean transform) {
        byte[] classBytes = getClassBytes(name, false);

        if (transform) {
            for (QuiltTransformer transformer : Launcher.getQuiltClassLoader().getTransformers()) {
                if (transformer instanceof QuiltMixinProxy) continue;
                classBytes = transformer.transform(name, classBytes);
            }
        }
        return classBytes;
    }

    boolean isClassLoaded(String name);

    URLClassLoader getClassLoader();

    void addURL(URL url);

    /**
     * The method checks with the implementation
     * whether by the time we attempt to get the class if it'll be available. If it isn't then
     * it should simply return false and we continue. This is to prevent constant try-catched
     * exceptions which can impact on performance when constant.
     * <p>
     * !! NOTE !!
     * <p>
     * THIS SHOULD NEVER BE CACHED
     *
     * @param name The name of the class to check whether is currently existence
     * @return Whether we'll be able to grab the class.
     */
    boolean isClassAvailable(String name);
}
