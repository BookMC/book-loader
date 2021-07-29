package org.bookmc.loader.impl.tweaker;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.BookMCLoaderCommon;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class BookMCClientLoader extends BookMCLoaderCommon {
    private final String target = System.getProperty("book.launch.target", "net.minecraft.client.main.Main");

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader, MixinEnvironment environment) {
    }

    @Override
    public Environment setEnvironment() {
        return Environment.CLIENT;
    }

    @Override
    public String getLaunchTarget() {
        return target;
    }
}
