package org.bookmc.loader.server;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.common.BookMCLoaderCommon;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class BookMCServerLoader extends BookMCLoaderCommon {
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        super.injectIntoClassLoader(classLoader);
        Mixins.addConfiguration("bookmc-server.mixins.json");
        MixinEnvironment.getCurrentEnvironment().setSide(MixinEnvironment.Side.SERVER);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }
}
