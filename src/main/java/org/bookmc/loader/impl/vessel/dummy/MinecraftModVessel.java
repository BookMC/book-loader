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

public class MinecraftModVessel implements ModVessel {
    private final String version;

    public MinecraftModVessel(String version) {
        this.version = version;
    }

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getId() {
        return "minecraft";
    }

    @Override
    public String getDescription() {
        return "Minecraft is a sandbox video game developed by the Swedish video game developer Mojang Studios. " +
            "The game was created by Markus \"Notch\" Persson in the Java programming language.";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public Author[] getAuthors() {
        return new Author[]{new Author("Mojang", "https://github.com/Mojang", null)};
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getLicense() {
        return "CUSTOM";
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
        return new File("there_is_no_mod_to_see");
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
