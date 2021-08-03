package org.bookmc.loader.impl.vessel.dummy;

import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.author.Author;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.launch.Launcher;

import java.io.File;
import java.net.URL;

public class JavaModVessel implements ModVessel {
    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public String getId() {
        return "java";
    }

    @Override
    public String getDescription() {
        return "Java is a set of computer software " +
            "and specifications developed by James Gosling at Sun Microsystems, " +
            "which was later acquired by the Oracle Corporation, " +
            "that provides a system for developing application software " +
            "and deploying it in a cross-platform computing environment.";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public Author[] getAuthors() {
        return new Author[]{new Author("Oracle Corporation", null, null)};
    }

    @Override
    public String getVersion() {
        return System.getProperty("java.version");
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
    public IQuiltClassLoader getAbstractedClassLoader() {
        return Launcher.getQuiltClassLoader();
    }

    @Override
    public void setClassLoader(IQuiltClassLoader classLoader) {

    }
}
