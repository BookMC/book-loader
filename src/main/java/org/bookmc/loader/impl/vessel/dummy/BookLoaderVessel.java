package org.bookmc.loader.impl.vessel.dummy;

import org.bookmc.loader.api.classloader.AbstractBookClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.author.Author;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.launch.BookLauncher;

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
    public Author[] getAuthors() {
        return new Author[]{new Author("BookMC", "https://github.com/BookMC", null), new Author("ChachyDev", "https://github.com/ChachyDev", null)};
    }

    @Override
    public String getVersion() {
        return "%LOADER_VERSION%";
    }

    @Override
    public String getIcon() {
        return "/assets/bookmc/icon/156x.png";
    }

    @Override
    public String getLicense() {
        return "%LICENSE%";
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
    public AbstractBookClassLoader getClassLoader() {
        return BookLauncher.getQuiltClassLoader();
    }

    @Override
    public void setClassLoader(AbstractBookClassLoader classLoader) {

    }
}
