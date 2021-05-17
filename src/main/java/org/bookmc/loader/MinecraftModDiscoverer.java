package org.bookmc.loader;

import com.google.gson.JsonParser;

import java.io.File;

public interface MinecraftModDiscoverer {
    JsonParser parser = new JsonParser();

    /**
     * A method to implement simple modloading
     *
     * @param files Array of files to be loaded via the implementation
     */
    void discover(File[] files);
}
