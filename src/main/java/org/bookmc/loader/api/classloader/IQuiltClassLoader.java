package org.bookmc.loader.api.classloader;

import org.bookmc.external.transformer.QuiltRemapper;
import org.bookmc.external.transformer.QuiltTransformer;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.launch.transform.mixin.QuiltMixinProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public interface IQuiltClassLoader {
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
                        // No point in giving transformers/remappers null just return.
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

    default CodeSource getCodeSource(String name) throws IOException {
        final int lastDot = name.lastIndexOf('.');
        final String fileName = name.replace('.', '/').concat(".class");
        URLConnection urlConnection = findCodeSourceConnectionFor(fileName);

        CodeSigner[] signers = null;

        if (lastDot > -1 && !name.startsWith("net.minecraft.")) {
            if (urlConnection instanceof final JarURLConnection jarURLConnection) {
                final JarFile jarFile = jarURLConnection.getJarFile();

                if (jarFile != null && jarFile.getManifest() != null) {
                    final JarEntry entry = jarFile.getJarEntry(fileName);

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
        final URL resource = getClassLoader().getResource(name);
        if (resource != null) {
            try {
                return resource.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    default byte[] getClassBytesMixin(String name, boolean transform) {
        byte[] classBytes = getClassBytes(name, false);

        if (transform) {
            for (QuiltTransformer transformer : Launcher.getQuiltClassLoader().getTransformers()) {
                if (transformer instanceof QuiltMixinProxy) continue; // Avoid re-entrance issues
                classBytes = transformer.transform(name, classBytes);
            }
        }
        return classBytes;
    }

    void putCachedClass(String name, byte[] bytes);
    boolean isClassAvailable(String name);
    byte[] getCachedClass(String name);
    boolean isClassLoaded(String name);
    URLClassLoader getClassLoader();
    void addURL(URL url);
}
