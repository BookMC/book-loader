package org.bookmc.loader.api.launch.transform;

public interface QuiltTransformer {
    byte[] transform(String name, byte[] clazz);
}
