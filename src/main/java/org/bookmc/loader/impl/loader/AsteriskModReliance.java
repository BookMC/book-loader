package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.mod.metadata.ModReliance;
import org.bookmc.loader.api.mod.metadata.ModVersion;
import org.bookmc.loader.api.mod.metadata.VersionIndicator;

public record AsteriskModReliance(String id, boolean required) implements ModReliance {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public ModVersion getRequestedVersion() {
        return null;
    }

    @Override
    public VersionIndicator getVersionIndicator() {
        return VersionIndicator.EQUAL_TO;
    }

    @Override
    public boolean isRequired() {
        return required;
    }
}
