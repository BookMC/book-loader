package org.bookmc.loader.client;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.common.BookMCLoaderCommon;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class BookMCClientLoader extends BookMCLoaderCommon {
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        super.injectIntoClassLoader(classLoader);

        Mixins.addConfiguration("bookmc-client.mixins.json");
        MixinEnvironment.getCurrentEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }
}
