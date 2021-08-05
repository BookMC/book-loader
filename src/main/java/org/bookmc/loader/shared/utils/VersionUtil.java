package org.bookmc.loader.shared.utils;

import com.github.zafarkhaja.semver.Version;
import org.bookmc.loader.shared.utils.operation.VersionOperation;

public class VersionUtil {
    public static boolean checkVersion(String requiredRaw, String givenRaw) {
        VersionOperation operation = VersionOperation.of(requiredRaw);
        Version required = Version.valueOf(VersionOperation.clean(requiredRaw));
        Version given = Version.valueOf(givenRaw);
        return operation.allow(required, given);
    }
}
