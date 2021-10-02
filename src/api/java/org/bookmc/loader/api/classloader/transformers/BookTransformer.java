package org.bookmc.loader.api.classloader.transformers;

public interface BookTransformer {
    default byte[] proposeTransformation(String name, byte[] clazz) {
        return transform(name, clazz);
    }

    /**
     * Deprecated in favour for "proposeTransformation", backwards compatibility will be removed in the future!
     * @param name Name of the class to be transformed
     * @param clazz If the transformer is the first element of the transformer list it will receive a clean class if not a transformed class
     * @return Transformed class data
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    byte[] transform(String name, byte[] clazz);
}
