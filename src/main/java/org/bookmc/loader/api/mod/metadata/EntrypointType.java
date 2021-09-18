package org.bookmc.loader.api.mod.metadata;

public enum EntrypointType {
    PRELAUNCH {
        @Override
        String getEntryMethod() {
            return "prelaunch";
        }
    },
    MAIN {
        @Override
        String getEntryMethod() {
            return "main";
        }
    },
    TRANSFORMER {
        @Override
        String getEntryMethod() {
            return null;
        }
    };

    abstract String getEntryMethod();
}
