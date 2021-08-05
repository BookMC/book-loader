package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.obfuscation.MinecraftObfuscationTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

            classBytes = MinecraftObfuscationTransformer.INSTANCE.transform(name, classBytes);

            if (transform) {
                for (QuiltTransformer transformer : Launcher.getQuiltClassLoader().getTransformers()) {
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
