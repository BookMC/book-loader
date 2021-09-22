package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.classloader.TransformableURLClassLoader;

import java.net.URL;

public class BookParentClassLoader extends TransformableURLClassLoader {
    public BookParentClassLoader() {
        super(new URL[0], BookParentClassLoader.class.getClassLoader());
    }
}
