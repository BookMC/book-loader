package org.bookmc.loader.api.environment;

import java.util.HashMap;
import java.util.Map;

public enum GameEnvironment {
    CLIENT {
        @Override
        public String acceptedValue() {
            return "client";
        }
    },
    SERVER {
        @Override
        public String acceptedValue() {
            return "server";
        }
    },
    UNIT_TEST {
        @Override
        public String acceptedValue() {
            return "unit_test";
        }
    },
    ANY {
        @Override
        public String acceptedValue() {
            return "*";
        }
    };
    private static final Map<String, GameEnvironment> environmentMap = new HashMap<>();

    static {
        for (GameEnvironment environment : values()) {
            environmentMap.put(environment.acceptedValue(), environment);
        }
    }

    public static GameEnvironment fromString(String value) {
        return environmentMap.get(value);
    }

    public abstract String acceptedValue();
}
