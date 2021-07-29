package org.bookmc.loader.impl.tweaker;

import org.bookmc.loader.api.BookMCLoaderCommon;
import org.bookmc.loader.api.vessel.environment.Environment;

public class BookMCClientLoader extends BookMCLoaderCommon {
    private final String target = System.getProperty("book.launch.target", "net.minecraft.client.main.Main");

    @Override
    public Environment setEnvironment() {
        return Environment.CLIENT;
    }

    @Override
    public String getLaunchTarget() {
        return target;
    }
}
