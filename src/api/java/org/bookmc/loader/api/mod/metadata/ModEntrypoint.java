package org.bookmc.loader.api.mod.metadata;

public interface ModEntrypoint {
    EntrypointType getEntrypointType();

    String getEntryClass();

    String getEntryMethod();

    static ModEntrypoint create(EntrypointType entrypointType, String entryClass) {
        return new ModEntrypoint() {
            @Override
            public EntrypointType getEntrypointType() {
                return entrypointType;
            }

            @Override
            public String getEntryClass() {
                return entryClass;
            }

            @Override
            public String getEntryMethod() {
                return entrypointType.getEntryMethod();
            }
        };
    }
}
