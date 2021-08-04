package org.bookmc.loader.impl.launch.transform;

import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuiltClassLoader extends URLClassLoader implements IQuiltClassLoader {
    private final Map<String, byte[]> classCache = new HashMap<>();
    private final List<QuiltTransformer> transformers = new ArrayList<>();
    private final List<String> exclusions = new ArrayList<>();

    public QuiltClassLoader() {
        super(new URL[0], QuiltClassLoader.class.getClassLoader());

        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("sun");
        addClassLoaderExclusion("org.lwjgl.");
        addClassLoaderExclusion("org.apache.logging.");
        addClassLoaderExclusion("org.bookmc.loader.");
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);

            if (c != null) {
                return c;
            }

            try {
                return findClass(name);
            } catch (ClassNotFoundException ignored) {
                return super.loadClass(name, resolve);
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (String exclusion : exclusions) {
            if (name.startsWith(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        if (isClassLoaded(name)) {
            return getLoadedClass(name);
        }
        byte[] bytes = getClassBytes(name, true);

        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }

        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public URL getResource(String name) {
        ClassLoader parent = getParent();
        URL url = super.getResource(name);
        return url != null ? url : parent.getResource(name);
    }

    @Override
    public byte[] getClassBytes(String name, boolean transformed) {
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

        if (transformed) {
            for (QuiltTransformer transformer : transformers) {
                classBytes = transformer.transform(name, classBytes);
            }
        }

        return classBytes;
    }

    @Override
    public URLClassLoader getClassLoader() {
        return this;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public ClassNode getClassNode(String name) {
        byte[] bytes = getClassBytes(name, true);
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);
        return node;
    }

    public boolean isClassLoaded(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name) != null;
        }
    }

    public Class<?> getLoadedClass(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name);
        }
    }

    public void registerTransformer(Class<? extends QuiltTransformer> transformer) {
        try {
            transformers.add((QuiltTransformer) loadClass(transformer.getName()).newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void addClassLoaderExclusion(String exclusion) {
        exclusions.add(exclusion);
    }
}
