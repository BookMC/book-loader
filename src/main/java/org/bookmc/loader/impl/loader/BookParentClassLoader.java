package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;

import java.net.URL;

public class BookParentClassLoader extends AbstractBookURLClassLoader {
    public BookParentClassLoader(URL[] urls) {
        super(urls, BookParentClassLoader.class.getClassLoader());
    }
}
