package org.bookmc.loader.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.EntrypointType;
import org.bookmc.loader.api.mod.metadata.ModEntrypoint;
import org.bookmc.loader.api.mod.metadata.ModMetadata;
import org.bookmc.loader.api.service.PrelaunchService;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class MixinPrelaunchService implements PrelaunchService {
    private final Logger LOGGER = LogManager.getLogger(this);

    @Override
    public void prelaunch(Map<String, String> arguments, GameEnvironment environment) {
        MixinBootstrap.init();

        BookLoaderBase.INSTANCE.getContainers()
            .values()
            .stream()
            .map(ModContainer::getMetadata)
            .map(ModMetadata::getEntrypoints)
            .flatMap(Arrays::stream)
            .filter(this::checkEntrypoint)
            .forEach(this::registerConfig);

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
        LOGGER.info("Setting Mixin Side to " + environment.name());
        MixinEnvironment.getDefaultEnvironment().setSide(toMixin(environment));
    }

    private MixinEnvironment.Side toMixin(GameEnvironment environment) {
        if (environment == GameEnvironment.CLIENT) return MixinEnvironment.Side.CLIENT;
        if (environment == GameEnvironment.SERVER) return MixinEnvironment.Side.SERVER;
        return MixinEnvironment.Side.UNKNOWN;
    }

    private boolean checkEntrypoint(ModEntrypoint entrypoint) {
        return entrypoint.getEntrypointType() == EntrypointType.MIXIN;
    }

    private void registerConfig(ModEntrypoint entrypoint) {
        LOGGER.info("Registering mixin config " + entrypoint.getEntryClass());
        Mixins.addConfiguration(entrypoint.getEntryClass());
    }
}
