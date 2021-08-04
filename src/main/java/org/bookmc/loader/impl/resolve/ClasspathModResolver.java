package org.bookmc.loader.impl.resolve;

import org.bookmc.loader.api.ModResolver;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.DirectoryModCandidate;
import org.bookmc.loader.impl.candidate.ZipModCandidate;
import org.bookmc.loader.impl.launch.Quilt;
import org.bookmc.loader.shared.Constants;
import org.bookmc.loader.shared.utils.ZipUtils;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class ClasspathModResolver implements ModResolver {
    @SuppressWarnings("unchecked")
    public static URL[] getClasspathURLs() {
        ClassLoader classLoader = Quilt.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader) classLoader).getURLs();
        }

        if (classLoader.getClass().getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Unsafe unsafe = (Unsafe) field.get(null);

                // jdk.internal.loader.ClassLoaders.AppClassLoader.ucp
                Field ucpField;
                try {
                    ucpField = classLoader.getClass().getDeclaredField("ucp");
                } catch (NoSuchFieldException | SecurityException e) {
                    ucpField = classLoader.getClass().getSuperclass().getDeclaredField("ucp");
                }

                long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
                Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);

                // jdk.internal.loader.URLClassPath.path
                Field pathField = ucpField.getType().getDeclaredField("path");
                long pathFieldOffset = unsafe.objectFieldOffset(pathField);
                ArrayList<URL> path = (ArrayList<URL>) unsafe.getObject(ucpObject, pathFieldOffset);

                return path.toArray(new URL[0]);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to find system class path URLs. Incompatible JDK?", e);
            }
        }
        return null;
    }

    @Override
    public void resolve(File[] files) {
        URL[] classpath = getClasspathURLs();
        if (classpath == null) {
            return;
        }

        for (URL url : classpath) {
            try {
                File file = new File(url.toURI());
                String name = file.getName();

                if (!name.endsWith(Constants.DISABLED_SUFFIX)) {
                    if (ZipUtils.isZipFile(file)) {
                        Loader.registerCandidate(new ZipModCandidate(new File(url.toURI())));
                    } else if (file.isDirectory()) {
                        Loader.registerCandidate(new DirectoryModCandidate(file));
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
