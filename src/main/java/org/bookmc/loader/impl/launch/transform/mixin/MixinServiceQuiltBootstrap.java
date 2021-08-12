package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.impl.launch.Launcher;
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
        if (Launcher.isDevelopment()) {
            // Have a break, have a kitkat Mixin
            System.setProperty("mixin.env.disableRefMap", "true");
        }
    }
}
