package org.bookmc.loader;

import java.io.File;

public interface ModVessel {
    String getName();

    String getId();

    String getDescription();

    String getConfig();

    @Deprecated
    String getAuthor();

    String[] getAuthors();

    String getVersion();

    String getEntrypoint();

    File getFile();

    String getMixinEntrypoint();

    String[] getDependencies();
}
