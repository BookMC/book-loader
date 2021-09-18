package org.bookmc.loader.impl.loader.reliance;

import org.bookmc.loader.api.mod.metadata.ModReliance;
import org.bookmc.loader.shared.version.VersionIndicator;

public record ModDependancyReliance(String id, String version,
                                    VersionIndicator versionIndicator) implements ModReliance {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getRequestedVersion() {
        return version;
    }

    @Override
    public VersionIndicator getVersionIndicator() {
        return versionIndicator;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
