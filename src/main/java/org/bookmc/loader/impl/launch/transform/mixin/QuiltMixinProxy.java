package org.bookmc.loader.impl.launch.transform.mixin;

import org.apache.logging.log4j.LogManager;
import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.spongepowered.asm.mixin.transformer.QuiltMixinTransformerProxy;
import org.spongepowered.asm.service.ILegacyClassTransformer;

import java.util.ArrayList;
import java.util.List;

public final class QuiltMixinProxy implements QuiltTransformer, ILegacyClassTransformer {

    /**
     * All existing proxies
     */
    private static final List<QuiltMixinProxy> proxies = new ArrayList<>();

    /**
     * Actual mixin transformer instance
     */
    private static final QuiltMixinTransformerProxy transformer = new QuiltMixinTransformerProxy();

    /**
     * True if this is the active proxy, newer proxies disable their older
     * siblings
     */
    private boolean isActive = true;

    public QuiltMixinProxy() {
        for (QuiltMixinProxy hook : proxies) {
            hook.isActive = false;
        }

        proxies.add(this);
        LogManager.getLogger("mixin")
            .debug("Adding new mixin transformer proxy #{}", QuiltMixinProxy.proxies.size());
    }

    @Override
    public byte[] transform(String name, byte[] basicClass) {
        return isActive ? transformer.transformClass(name, name, basicClass) : basicClass;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isDelegationExcluded() {
        return true;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        return isActive ? transformer.transformClass(name, transformedName, basicClass) : basicClass;
    }
}