package org.bookmc.loader.api.mod.metadata;

public enum VersionIndicator {
    GREATER_THAN(1),
    EQUAL_TO(0),
    LESS_THAN(-1);

    private final int compareInt;

    VersionIndicator(int compareInt) {
        this.compareInt = compareInt;
    }

    public int getCompareInt() {
        return compareInt;
    }

    public static VersionIndicator fromVersion(String version) {
        if (version.equals("<")) return GREATER_THAN;
        if (version.equals(">")) return LESS_THAN;
        return EQUAL_TO;
    }

    public static String removeVersionIndicator(String version) {
        return version.replace(">", "").replace("<", "");
    }

    public static VersionIndicator fromInt(int i) {
        for (VersionIndicator indicator : values()) {
            if (indicator.compareInt == i) {
                return indicator;
            }
        }

        return null;
    }
}
