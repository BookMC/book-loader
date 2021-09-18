package org.bookmc.loader.impl.loader.reliance;

import org.bookmc.loader.api.mod.metadata.ModReliance;
import org.bookmc.loader.shared.version.VersionIndicator;

public record ModSuggestionReliance(String id, String requestedVersion,
                                    VersionIndicator versionIndicator) implements ModReliance {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getRequestedVersion() {
        return requestedVersion;
    }

    @Override
    public VersionIndicator getVersionIndicator() {
        return versionIndicator;
    }

    @Override
    public boolean isRequired() {
        return false;
    }
}
