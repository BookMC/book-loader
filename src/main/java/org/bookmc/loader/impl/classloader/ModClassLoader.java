package org.bookmc.loader.impl.classloader;

import org.bookmc.loader.api.classloader.AbstractBookClassLoader;
import org.bookmc.loader.impl.launch.BookLauncher;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;

public class ModClassLoader extends AbstractBookClassLoader {
    public ModClassLoader(URL[] urls) {
        super(urls, BookLauncher.getQuiltClassLoader());
        addClassLoaderExclusion("net.minecraft."); // We don't want minecraft on our classloader!
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> initialClass;
        initialClass = checkExclusion(BookLauncher.getQuiltClassLoader().getExclusions(), name);
        if (initialClass != null) {
            return initialClass;
        }

        initialClass = checkExclusion(exclusions, name);
        if (initialClass != null) {
            return initialClass;
        }

        try {
            CodeSource source = getCodeSource(name);
            byte[] bytes = getClassBytes(name, true);

            if (bytes == null) {
                throw new ClassNotFoundException(name);
            }


            return defineClass(name, bytes, 0, bytes.length, source);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
