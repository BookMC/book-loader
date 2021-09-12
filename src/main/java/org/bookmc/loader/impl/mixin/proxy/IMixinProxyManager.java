package org.bookmc.loader.impl.mixin.proxy;

import org.bookmc.loader.impl.mixin.BookMixinProxy;
import org.spongepowered.asm.mixin.transformer.BookMixinTransformer;

public interface IMixinProxyManager {
    void registerProxy(BookMixinProxy proxy);
    void createProxy();
    BookMixinTransformer getTransformer();
}
