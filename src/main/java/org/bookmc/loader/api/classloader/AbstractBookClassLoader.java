package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.transformer.QuiltRemapper;
import org.bookmc.loader.api.transformer.QuiltTransformer;
import org.bookmc.loader.impl.launch.BookLauncher;
import org.bookmc.loader.impl.mixin.BookMixinProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class AbstractBookClassLoader extends URLClassLoader {
    private final Map<String, byte[]> classCache = new HashMap<>();
    protected final List<String> exclusions = new ArrayList<>();

    public AbstractBookClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);

            if (c == null) {
                c = findClass(name);
            }

            if (resolve) {
                resolveClass(c);
            }

            return c;
        }
    }

    public Class<?> checkExclusion(List<String> exclusions, String name) throws ClassNotFoundException {
        for (String exclusion : exclusions) {
            if (name.startsWith(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        return null;
    }

    public byte[] getClassBytes(String name, boolean transform) {
        byte[] classBytes = classCache.get(name);

        if (classBytes == null) {
            try {
                try (InputStream stream = getResourceAsStream(name.replace(".", "/").concat(".class"))) {
                    if (stream != null) {
                        classBytes = stream.readAllBytes();
                        // Cached class data should always be clean...
                        classCache.put(name, classBytes);
                    } else {
                        // No point in giving transformers/remappers null just return.
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (QuiltRemapper remapper : BookLauncher.getQuiltClassLoader().getRemappers()) {
            classBytes = remapper.transform(name, classBytes);
        }

        if (transform) {
            for (QuiltTransformer transformer : BookLauncher.getQuiltClassLoader().getTransformers()) {
                classBytes = transformer.transform(name, classBytes);
            }
        }

        return classBytes;
    }

    public CodeSource getCodeSource(String name) throws IOException {
        int lastDot = name.lastIndexOf('.');
        String fileName = name.replace('.', '/').concat(".class");
        URLConnection urlConnection = findCodeSourceConnectionFor(fileName);

        CodeSigner[] signers = null;

        if (lastDot > -1 && !name.startsWith("net.minecraft.")) {
            if (urlConnection instanceof final JarURLConnection jarURLConnection) {
                final JarFile jarFile = jarURLConnection.getJarFile();

                if (jarFile != null && jarFile.getManifest() != null) {
                    JarEntry entry = jarFile.getJarEntry(fileName);

                    if (entry != null) {
                        signers = entry.getCodeSigners();
                    }
                }
            }
        }

        return urlConnection == null ? null : new CodeSource(fixJarURL(urlConnection instanceof JarURLConnection ? ((JarURLConnection) urlConnection).getJarFileURL() : urlConnection.getURL(), name), signers);
    }

    private URL fixJarURL(URL url, String className) throws MalformedURLException {
        String toString = url.toString();

        if (toString.startsWith("jar:")) {
            return new URL(toString.substring(4));
        }

        String classNamePath = "/".concat(className.replace(".", "/")).concat(".class");

        if (toString.endsWith(classNamePath)) {
            return new URL(toString.substring(0, toString.length() - classNamePath.length()));
        }

        return url;
    }

    private URLConnection findCodeSourceConnectionFor(String name) {
        final URL resource = getResource(name);
        if (resource != null) {
            try {
                return resource.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public byte[] getClassBytesMixin(String name, boolean transform) {
        byte[] classBytes = getClassBytes(name, false);

        if (transform) {
            for (QuiltTransformer transformer : BookLauncher.getQuiltClassLoader().getTransformers()) {
                if (transformer instanceof BookMixinProxy) continue; // Avoid re-entrance issues
                classBytes = transformer.transform(name, classBytes);
            }
        }
        return classBytes;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void addClassLoaderExclusion(String toExclude) {
        exclusions.add(toExclude);
    }

    public boolean isClassLoaded(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name) != null;
        }
    }

    public boolean isClassLocatable(String name) {
        return getResource(name.replace(".", "/").concat(".class")) != null;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
