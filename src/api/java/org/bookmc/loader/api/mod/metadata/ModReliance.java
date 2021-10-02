package org.bookmc.loader.api.mod.metadata;

public interface ModReliance {
    String getId();

    ModVersion getRequestedVersion();

    VersionIndicator getVersionIndicator();

    boolean isRequired();
}