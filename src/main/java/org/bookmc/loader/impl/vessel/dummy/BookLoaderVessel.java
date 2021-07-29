package org.bookmc.loader.impl.vessel.dummy;

import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;

import java.io.File;
import java.net.URL;

public class BookLoaderVessel implements ModVessel {
    @Override
    public String getName() {
        return "book-loader";
    }

    @Override
    public String getId() {
        return "book-loader";
    }

    @Override
    public String getDescription() {
        return "The version independent loader for Book";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public String[] getAuthors() {
        return new String[]{"BookMC", "ChachyDev"};
    }

    @Override
    public String getVersion() {
        return "%LOADER_VERSION%";
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getEntrypoint() {
        return null;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public String getMixinEntrypoint() {
        return null;
    }

    @Override
    public ModDependency[] getDependsOn() {
        return new ModDependency[0];
    }

    @Override
    public ModDependency[] getSuggestions() {
        return new ModDependency[0];
    }

    @Override
    public URL[] getExternalDependencies() {
        return new URL[0];
    }

    @Override
    public boolean isCompatibilityLayer() {
        return false;
    }
}
