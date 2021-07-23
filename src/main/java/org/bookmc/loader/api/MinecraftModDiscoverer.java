package org.bookmc.loader.api;

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

    default boolean isFilesRequired() {
        return true;
    }
}
