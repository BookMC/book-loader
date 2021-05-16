package org.bookmc.loader.server;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.common.BookMCLoaderCommon;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class BookMCServerLoader extends BookMCLoaderCommon {
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader, MixinEnvironment environment) {
        Mixins.addConfiguration("bookmc-server.mixins.json");

    }

    @Override
    public void setSide(MixinEnvironment environment) {
        environment.setSide(MixinEnvironment.Side.SERVER);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }
}
