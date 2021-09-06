package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

public final class QuiltMixinProxy implements QuiltTransformer, ILegacyClassTransformer {
    private final QuiltMixinProxyManager manager;
    private boolean active = true;

    public QuiltMixinProxy(QuiltMixinProxyManager manager) {
        this.manager = manager;
    }

    @Override
    public byte[] transform(String name, byte[] basicClass) {
        return active ? manager.getTransformer().transformClass(name, name, basicClass) : basicClass;
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
        return active ? manager.getTransformer().transformClass(name, transformedName, basicClass) : basicClass;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}