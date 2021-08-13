package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.launch.transform.QuiltRemapper;
import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.launch.transform.mixin.QuiltMixinProxy;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public interface IQuiltClassLoader {
    byte[] getCachedClass(String name);

    void putCachedClass(String name, byte[] bytes);

    default byte[] getClassBytes(String name, boolean transform) {
        InputStream resource = getClassLoader().getResourceAsStream(name.replace(".", "/").concat(".class"));

        if (resource == null) {
            return null;
        }

        byte[] classBytes = getCachedClass(name);

        if (classBytes == null) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int read;
                byte[] buffer = new byte[1024];
                while ((read = resource.read(buffer, 0, buffer.length)) != -1) {
                    baos.write(buffer, 0, read);
                }
                classBytes = baos.toByteArray();
                putCachedClass(name, classBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // The remappers most likely use asm so lets fix the chance of a ClassCircularityError
            if (!name.startsWith("org.objectweb.asm.")) {
                for (QuiltRemapper remapper : Launcher.getQuiltClassLoader().getRemappers()) {
                    classBytes = remapper.transform(name, classBytes);
                }
            }

            if (transform) {
                for (QuiltTransformer transformer : Launcher.getQuiltClassLoader().getTransformers()) {
                    if (transformer instanceof QuiltMixinProxy) continue;
                    classBytes = transformer.transform(name, classBytes);
                }
            }
        }

        return classBytes;
    }

    boolean isClassLoaded(String name);

    URLClassLoader getClassLoader();

    void addURL(URL url);
}
