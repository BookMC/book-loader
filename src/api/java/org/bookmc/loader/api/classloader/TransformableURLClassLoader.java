package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.classloader.transformers.BookTransformer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class TransformableURLClassLoader extends AbstractBookURLClassLoader {
    protected final List<BookTransformer> transformers = new ArrayList<>();
    protected final List<String> transformationExclusions = new ArrayList<>();

    public TransformableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public byte[] modifyResolvedBytes(String name, byte[] bytes) {
        return !transformationExclusions.contains(name) ? transformClass(name, bytes) : bytes;
    }

    public byte[] transformClass(String name, byte[] clazz) {
        for (BookTransformer transformer : transformers) {
            clazz = transformer.proposeTransformation(name, clazz);
        }
        return clazz;
    }

    public void registerTransformer(BookTransformer transformer) {
        transformers.add(transformer);
    }
}
