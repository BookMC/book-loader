package org.bookmc.loader.shared.utils.operation;

import com.github.zafarkhaja.semver.Version;

public enum VersionOperation {
    GREATER_THAN {
        @Override
        public boolean allow(Version required, Version other) {
            return other.greaterThan(required);
        }
    },
    GREATER_THAN_OR_EQUAL_TO {
        @Override
        public boolean allow(Version required, Version other) {
            return other.greaterThanOrEqualTo(required);
        }
    },
    LESS_THAN {
        @Override
        public boolean allow(Version required, Version other) {
            return other.lessThan(required);
        }
    },
    LESS_THAN_OR_EQUAL_TO {
        @Override
        public boolean allow(Version required, Version other) {
            return other.lessThanOrEqualTo(required);
        }
    },
    EQUALS {
        @Override
        public boolean allow(Version required, Version other) {
            return required.equals(other);
        }
    };

    public static VersionOperation of(String required) {
        if (required.startsWith(">=")) return GREATER_THAN_OR_EQUAL_TO;
        if (required.startsWith("<=")) return LESS_THAN_OR_EQUAL_TO;
        if (required.startsWith(">")) return GREATER_THAN;
        if (required.startsWith("<")) return LESS_THAN;
        return EQUALS;
    }

    public static String clean(String current) {
        return current.replace(">=", "")
            .replace("<=", "")
            .replace(">", "")
            .replace(">", "");
    }

    public abstract boolean allow(Version required, Version other);
}
