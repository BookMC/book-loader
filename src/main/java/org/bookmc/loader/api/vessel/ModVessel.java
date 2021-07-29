package org.bookmc.loader.api.vessel;

import org.bookmc.loader.api.vessel.dependency.ModDependency;

import java.io.File;
import java.net.URL;

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
    String[] getAuthors();

    // Get version of the mod
    String getVersion();

    // Get the entrypoint of the mod
    String getEntrypoint();

    // Return the location of the mod (If it is a jar/zip mod)
    File getFile();

    // Returns the mixin entrypoint file (if available)
    String getMixinEntrypoint();

    // Returns the dependencies of the mod
    ModDependency[] getDependsOn();

    ModDependency[] getSuggestions();

    URL[] getExternalDependencies();

    String getIcon();

    boolean isInternallyEnabled();

    void setInternallyEnabled(boolean enabled);

    boolean isCompatibilityLayer();
}
