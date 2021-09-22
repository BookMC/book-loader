package org.bookmc.loader.impl.loader.resolver;

import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.impl.launch.Book;
import org.bookmc.loader.impl.loader.BookLoaderImpl;
import org.bookmc.loader.impl.loader.candidate.ResourceModCandidate;
import org.bookmc.loader.impl.loader.candidate.ZipModCandidate;
import org.bookmc.loader.shared.zip.ZipUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClasspathModResolver implements ModResolver {
    @SuppressWarnings({"restriction", "unchecked"})
    public static URL[] getSystemClassPathURLs() {
        ClassLoader classLoader = Book.class.getClassLoader();
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

        return new URL[0];
    }

    @Override
    public ModCandidate[] resolveMods() {
        List<Path> classpath = Arrays.stream(getSystemClassPathURLs())
            .map(u -> {
                try {
                    return Path.of(u.toURI());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        List<ModCandidate> candidates = new ArrayList<>();

        for (Path item : classpath) {
            if (ZipUtils.isZipFile(item)) {
                candidates.add(new ZipModCandidate(item));
            }
        }

        candidates.add(new ResourceModCandidate(BookLoaderImpl.INSTANCE.getGlobalClassLoader()));
        return candidates.toArray(new ModCandidate[0]);
    }
}
