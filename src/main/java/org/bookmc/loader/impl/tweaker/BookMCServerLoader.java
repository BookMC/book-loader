package org.bookmc.loader.impl.tweaker;

import org.bookmc.loader.api.BookMCLoaderCommon;
import org.bookmc.loader.api.vessel.environment.Environment;

public class BookMCServerLoader extends BookMCLoaderCommon {
    private final String target = System.getProperty("book.launch.target", "net.minecraft.server.MinecraftServer");

    @Override
    public Environment setEnvironment() {
        return Environment.SERVER;
    }

    @Override
    public String getLaunchTarget() {
        return target;
    }
}
