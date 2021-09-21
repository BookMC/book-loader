package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.mod.metadata.ModReliance;
import org.bookmc.loader.api.mod.metadata.ModVersion;
import org.bookmc.loader.api.mod.metadata.VersionIndicator;

public record ModDependancyReliance(String id, ModVersion version,
                                    VersionIndicator versionIndicator, boolean required) implements ModReliance {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public ModVersion getRequestedVersion() {
        return version;
    }

    @Override
    public VersionIndicator getVersionIndicator() {
        return versionIndicator;
    }

    @Override
    public boolean isRequired() {
        return required;
    }
}
