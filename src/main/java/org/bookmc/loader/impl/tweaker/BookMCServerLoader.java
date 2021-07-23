package org.bookmc.loader.impl.tweaker;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.BookMCLoaderCommon;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class BookMCServerLoader extends BookMCLoaderCommon {
    private final String target = System.getProperty("book.launch.target", "net.minecraft.server.MinecraftServer");

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader, MixinEnvironment environment) {
        Mixins.addConfiguration("bookmc-server.mixins.json");

    }

    @Override
    public MixinEnvironment.Side setSide(MixinEnvironment environment) {
        environment.setSide(MixinEnvironment.Side.SERVER);

        return MixinEnvironment.Side.SERVER;
    }

    @Override
    public String getLaunchTarget() {
        return target;
    }
}
