package org.bookmc.loader.api.mod.metadata;

import org.bookmc.loader.shared.version.VersionIndicator;

public interface ModReliance {
    String getId();
    String getRequestedVersion();
    VersionIndicator getVersionIndicator();
    boolean isRequired();
}