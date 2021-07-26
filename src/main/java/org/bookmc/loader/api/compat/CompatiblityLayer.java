package org.bookmc.loader.api.compat;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.BookMCLoaderCommon;

public interface CompatiblityLayer {
    void init(BookMCLoaderCommon common, LaunchClassLoader classLoader);
}
