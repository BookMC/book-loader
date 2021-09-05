package org.bookmc.external.transformer;

public interface QuiltTransformer {
    byte[] transform(String name, byte[] clazz);
}
