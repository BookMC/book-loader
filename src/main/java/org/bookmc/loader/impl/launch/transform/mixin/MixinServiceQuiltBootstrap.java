package org.bookmc.loader.impl.launch.transform.mixin;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class MixinServiceQuiltBootstrap implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "Quilt";
    }

    @Override
    public String getServiceClassName() {
        return "org.bookmc.loader.impl.launch.transform.mixin.MixinServiceQuilt";
    }

    @Override
    public void bootstrap() {

    }
}
