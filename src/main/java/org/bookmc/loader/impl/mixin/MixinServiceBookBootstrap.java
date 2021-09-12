package org.bookmc.loader.impl.mixin;

import org.bookmc.loader.impl.launch.BookLauncher;
import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class MixinServiceBookBootstrap implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "Quilt";
    }

    @Override
    public String getServiceClassName() {
        return "org.bookmc.loader.impl.mixin.MixinServiceBook";
    }

    @Override
    public void bootstrap() {
        if (BookLauncher.isDevelopment()) {
            // Have a break, have a kitkat Mixin
            System.setProperty("mixin.env.disableRefMap", "true");
        }

        String[] exclusions = new String[]{
            "org.spongepowered.asm.service.",
            "org.spongepowered.asm.launch.",
            "org.spongepowered.asm.logging.",
            "org.spongepowered.asm.util.",
            "org.spongepowered.asm.lib.",
            "org.objectweb.asm.",
            "org.spongepowered.asm.mixin."
        };

        for (String exclusion : exclusions) {
            BookLauncher.getQuiltClassLoader().addClassLoaderExclusion(exclusion);
        }
    }
}
