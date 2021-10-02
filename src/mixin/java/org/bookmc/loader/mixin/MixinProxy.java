package org.bookmc.loader.mixin;

import org.bookmc.loader.api.classloader.transformers.BookTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

public final class MixinProxy implements BookTransformer, ILegacyClassTransformer {
    private final MixinProxyManager manager;
    private boolean active = true;

    public MixinProxy(MixinProxyManager manager) {
        this.manager = manager;
    }

    @Override
    public byte[] proposeTransformation(String name, byte[] basicClass) {
        return active ? manager.getTransformer().transformClassBytes(name, name, basicClass) : basicClass;
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
        return active ? manager.getTransformer().transformClassBytes(name, transformedName, basicClass) : basicClass;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}