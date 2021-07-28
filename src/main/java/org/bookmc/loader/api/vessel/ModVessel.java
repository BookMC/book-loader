package org.bookmc.loader.api.vessel;

import java.io.File;

public interface ModVessel {
    // Name of the mod
    String getName();

    // Id of the mod
    String getId();

    // Description of what the mod does
    String getDescription();

    // Returns the config class string of the mod
    String getConfig();

    // (Deprecated) Returns the author of the mod.
    @Deprecated
    String getAuthor();

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
    String[] getDependencies();

    String getIcon();

    boolean isInternallyEnabled();

    void setInternallyEnabled(boolean enabled);

    boolean isCompatibilityLayer();
}
