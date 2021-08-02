package org.bookmc.loader.impl.vessel.dummy;

import net.minecraft.launchwrapper.Launch;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.author.Author;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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
    public Author[] getAuthors() {
        return new Author[]{new Author("BookMC", "https://github.com/BookMC", null), new Author("ChachyDev", "https://github.com/ChachyDev", null)};
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
    public String getLicense() {
        return null;
    }

    @Override
    public Entrypoint[] getEntrypoints() {
        return new Entrypoint[0];
    }

    @Override
    public Environment getEnvironment() {
        return Environment.ANY;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public MixinEntrypoint[] getMixinEntrypoints() {
        return new MixinEntrypoint[0];
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
    public URLClassLoader getClassLoader() {
        return Launch.classLoader;
    }

    @Override
    public void setClassLoader(URLClassLoader classLoader) {

    }
}
