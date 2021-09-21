package org.bookmc.loader.mixin;

import org.bookmc.loader.api.loader.BookLoaderBase;
import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class BookMixinServiceBootstrap implements IMixinServiceBootstrap {
    private final boolean isDevelopment = System.getenv().containsKey("PG_MAIN_CLASS") || System.getProperties().containsKey("book.development");

    @Override
    public String getName() {
        return "Book";
    }

    @Override
    public String getServiceClassName() {
        return "org.bookmc.loader.mixin.MixinServiceBook";
    }

    @Override
    public void bootstrap() {
        if (isDevelopment) {
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
            BookLoaderBase.INSTANCE.getGlobalClassLoader().addClassLoaderExclusion(exclusion);
        }
    }
}
