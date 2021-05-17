package org.bookmc.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class BookModClassLoader extends URLClassLoader {
    public BookModClassLoader(File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, BookModClassLoader.class.getClassLoader());
    }
}
