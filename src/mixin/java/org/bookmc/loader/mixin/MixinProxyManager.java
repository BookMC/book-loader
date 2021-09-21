package org.bookmc.loader.mixin;

import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.mixin.state.MixinStateManager;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.util.ArrayList;
import java.util.List;

public class MixinProxyManager {
    public static MixinProxyManager INSTANCE = new MixinProxyManager();

    private final List<MixinProxy> proxies = new ArrayList<>();

    public void registerProxy(MixinProxy proxy) {
        BookLoaderBase.INSTANCE.getGlobalClassLoader().registerTransformer(proxy);

        // Disable all current proxies
        for (MixinProxy mixinProxy : proxies) {
            mixinProxy.setActive(false);
        }
        // Add our new proxy
        proxies.add(proxy);
    }

    public void createProxy() {
        registerProxy(new MixinProxy(this));
    }

    public IMixinTransformer getTransformer() {
        return MixinStateManager.getTransformer();
    }
}