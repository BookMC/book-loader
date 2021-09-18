package org.bookmc.loader.shared.version;

public enum VersionIndicator {
    GREATER_THAN,
    LESS_THAN,
    EQUAL_TO;

    public static VersionIndicator fromVersion(String version) {
        if (version.equals("<")) return GREATER_THAN;
        if (version.equals(">")) return LESS_THAN;
        return EQUAL_TO;
    }

    public static String removeVersionIndicator(String version) {
        return version.replace(">=", "")
            .replace("<=", "")
            .replace(">", "")
            .replace("<", "");
    }
}
