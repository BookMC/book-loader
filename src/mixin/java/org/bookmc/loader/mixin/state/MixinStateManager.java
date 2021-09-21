package org.bookmc.loader.mixin.state;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

public class MixinStateManager {
    private static IMixinTransformer transformer;

    public static void setTransformer(IMixinTransformer transformer) {
        MixinStateManager.transformer = transformer;
    }

    public static IMixinTransformer getTransformer() {
        return transformer;
    }
}
