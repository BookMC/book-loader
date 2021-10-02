package org.bookmc.loader.api.classloader.transformers;

public interface BookTransformer {
    byte[] proposeTransformation(String name, byte[] clazz);
}
