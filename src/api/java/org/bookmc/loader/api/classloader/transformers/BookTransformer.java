package org.bookmc.loader.api.classloader.transformers;

public interface BookTransformer {
    byte[] transform(String name, byte[] clazz);
}
