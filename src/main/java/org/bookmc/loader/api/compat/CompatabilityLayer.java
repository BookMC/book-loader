package org.bookmc.loader.api.compat;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.BookMCLoaderCommon;

public interface CompatabilityLayer {
    void init(BookMCLoaderCommon common, LaunchClassLoader classLoader);
}
