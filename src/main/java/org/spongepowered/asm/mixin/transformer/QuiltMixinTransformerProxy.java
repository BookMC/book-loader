package org.spongepowered.asm.mixin.transformer;

/**
 * Due to MixinTransformer being package-private we must create this class in
 * the package and allow others to access it from there by making it a public class.
 */
public class QuiltMixinTransformerProxy {
    private final MixinTransformer transformer = new MixinTransformer();

    public byte[] transformClass(String name, String transformedName, byte[] clazz) {
        return transformer.transformClassBytes(name, transformedName, clazz);
    }
}
