package org.bookmc.loader.api.transformer;

public interface QuiltTransformer {
    byte[] transform(String name, byte[] clazz);
}
