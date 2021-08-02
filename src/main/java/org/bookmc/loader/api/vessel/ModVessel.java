package org.bookmc.loader.api.vessel;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.vessel.author.Author;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public interface ModVessel {
    // Name of the mod
    String getName();

    // Id of the mod
    String getId();

    // Description of what the mod does
    String getDescription();

    // Returns the config class string of the mod
    String getConfig();

    // Returns the authors of the mod
    Author[] getAuthors();

    // Get version of the mod
    String getVersion();

    // Get an (optional) icon
    String getIcon();

    String getLicense();

    // Get the entrypoint of the mod
    Entrypoint[] getEntrypoints();

    Environment getEnvironment();

    // Return the location of the mod (If it is a jar/zip mod)
    File getFile();

    // Returns the mixin entrypoint file (if available)
    MixinEntrypoint[] getMixinEntrypoints();

    // Returns the dependencies of the mod
    ModDependency[] getDependsOn();

    ModDependency[] getSuggestions();

    URL[] getExternalDependencies();

    URLClassLoader getClassLoader();

    void setClassLoader(URLClassLoader classLoader);

    default boolean isInternal() {
        return getClassLoader() instanceof LaunchClassLoader;
    }
}
