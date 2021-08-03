package org.bookmc.loader.api.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ModClassLoader extends URLClassLoader implements IQuiltClassLoader {
    private final Map<String, byte[]> classCache = new HashMap<>();

    public ModClassLoader(URL[] urls) {
        super(urls, ModClassLoader.class.getClassLoader());
    }

    @Override
    public byte[] getClassBytes(String name, boolean transform) {
        if (transform) {
            throw new UnsupportedOperationException("This feature is currently not supported by mod classloaders!");
        }

        InputStream resource = getResourceAsStream(name.replace(".", "/").concat(".class"));

        if (resource == null) {
            return null;
        }

        byte[] classBytes = classCache.get(name);

        if (classBytes == null) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int read;
                byte[] buffer = new byte[1024];
                while ((read = resource.read(buffer, 0, buffer.length)) != -1) {
                    baos.write(buffer, 0, read);
                }
                classBytes = baos.toByteArray();
                classCache.put(name, classBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return classBytes;
    }

    @Override
    public URLClassLoader getClassLoader() {
        return this;
    }
}
