package org.bookmc.loader.impl.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.launch.BookLauncher;
import org.bookmc.loader.impl.mixin.proxy.IMixinProxyManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.reflect.Method;

public class BookMixinBootstrap {
    private static final Logger LOGGER = LogManager.getLogger(BookMixinBootstrap.class);
    private static IMixinProxyManager proxyManager;

    public static void init() {
        MixinBootstrap.init();
        try {
            Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            LOGGER.info("Moving Mixin Phase to INIT");
            m.invoke(null, MixinEnvironment.Phase.INIT);
            LOGGER.info("Moving Mixin Phase to DEFAULT");
            m.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            LOGGER.fatal("Mixin error!");
            throw new RuntimeException("Failed to bootstrap Mixin", e);
        }
        LOGGER.info("Setting Mixin Side to " + BookLauncher.getEnvironment().name());
        MixinEnvironment.getDefaultEnvironment().setSide(Environment.toMixin(BookLauncher.getEnvironment()));
    }

    public static void loadMixins(Environment environment) {
        for (ModVessel vessel : Loader.getModVessels()) {
            loadMixin(vessel, environment);
        }
    }

    public static void loadMixin(ModVessel vessel, Environment environment) {
        MixinEntrypoint[] mixinEntrypoints = vessel.getMixinEntrypoints();

        for (MixinEntrypoint entrypoint : mixinEntrypoints) {
            if (environment.allows(entrypoint.getEnvironment())) {
                Mixins.addConfiguration(entrypoint.getMixinFile());
            }
        }
    }

    public static void setProxyManager(IMixinProxyManager proxyManager) {
        if (BookMixinBootstrap.proxyManager != null) {
            throw new IllegalStateException("You cannot change the Mixin proxy manager once it has been set, rethink your logic!");
        }
        BookMixinBootstrap.proxyManager = proxyManager;
    }

    public static IMixinProxyManager getProxyManager() {
        if (proxyManager == null) {
            setProxyManager(new BookMixinProxyManager());
        }
        return proxyManager;
    }
}
