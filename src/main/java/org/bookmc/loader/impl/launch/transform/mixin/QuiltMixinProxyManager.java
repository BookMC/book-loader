package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.impl.launch.Launcher;
import org.spongepowered.asm.mixin.transformer.QuiltMixinTransformer;

import java.util.ArrayList;
import java.util.List;

public class QuiltMixinProxyManager {
    private final QuiltMixinTransformer transformer = new QuiltMixinTransformer();
    private final List<QuiltMixinProxy> proxies = new ArrayList<>();

    public void registerProxy(QuiltMixinProxy proxy) {
        Launcher.getQuiltClassLoader().registerTransformer(proxy);

        // Disable all current proxies
        for (QuiltMixinProxy quiltMixinProxy : proxies) {
            quiltMixinProxy.setActive(false);
        }
        // Add our new proxy
        proxies.add(proxy);
    }

    public void createProxy() {
        registerProxy(new QuiltMixinProxy(this));
    }

    public QuiltMixinTransformer getTransformer() {
        return transformer;
    }
}
