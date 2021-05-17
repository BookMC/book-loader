package org.bookmc.loader.client;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.common.BookMCLoaderCommon;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class BookMCClientLoader extends BookMCLoaderCommon {
    private final String target = System.getProperty("book.launch.target", "net.minecraft.client.main.Main");

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader, MixinEnvironment environment) {
        Mixins.addConfiguration("bookmc-client.mixins.json");
    }

    @Override
    public void setSide(MixinEnvironment environment) {
        environment.setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String getLaunchTarget() {
        return target;
    }
}
