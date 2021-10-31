package org.bookmc.loader.impl.loader.version;

import com.github.zafarkhaja.semver.Version;
import org.bookmc.loader.api.mod.metadata.ModVersion;

import javax.annotation.Nonnull;

public class ModSemverVersion implements ModVersion {
    private final String version;
    private final Version semverVersion;

    public ModSemverVersion(String version) {
        this.version = version;

        try {
            this.semverVersion = Version.valueOf(version);
        } catch (Throwable t) {
            throw new RuntimeException("The metadata parser requires you follow semver to function!", t);
        }
    }

    @Override
    public String getRawVersion() {
        return version;
    }

    public Version getSemverVersion() {
        return semverVersion;
    }

    @Override
    public int compareTo(@Nonnull ModVersion version) {
        // Instead of causing a massive kerfuffle over nothing just pretend it's equal
        // If this breaks stuff this is probably someone who is messing with internals
        // fault (not really) but like it would work if they didnt so please don't th-
        // ank you
        if (!(version instanceof ModSemverVersion)) return 0;
        return semverVersion.compareTo(((ModSemverVersion) version).getSemverVersion());
    }

}
