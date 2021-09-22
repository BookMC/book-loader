package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.classloader.transformers.BookTransformer;

import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

public abstract class TransformableURLClassLoader extends AbstractBookURLClassLoader {
    protected final List<BookTransformer> transformers = new ArrayList<>();
    protected final List<String> transformationExclusions = new ArrayList<>();

    public TransformableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (String exclusion : classLoaderExclusions) {
            if (name.startsWith(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        byte[] clazz = getClassAsBytes(name);

        if (!transformationExclusions.contains(name)) {
            clazz = transformClass(name, clazz);
        }

        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }

        // TODO: Implement CodeSigner
        return defineClass(name, clazz, 0, clazz.length, new CodeSource(getClass(name), new CodeSigner[0]));
    }

    public byte[] transformClass(String name, byte[] clazz) {
        for (BookTransformer transformer : transformers) {
            clazz = transformer.transform(name, clazz);
        }
        return clazz;
    }

    public void registerTransformer(BookTransformer transformer) {
        transformers.add(transformer);
    }
}
