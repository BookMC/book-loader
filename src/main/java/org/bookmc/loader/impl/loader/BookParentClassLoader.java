package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;

import java.net.URL;

public class BookParentClassLoader extends AbstractBookURLClassLoader {
    public BookParentClassLoader() {
        super(new URL[0], BookParentClassLoader.class.getClassLoader(), true);
    }

    @Override
    public URL findResource(String name) {
        URL resource = super.findResource(name);
        return resource != null ? resource : getParent().getResource(name);
    }
}
