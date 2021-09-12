package org.bookmc.loader.api.vessel.entrypoint;

import org.bookmc.loader.api.vessel.environment.Environment;

public record MixinEntrypoint(String mixinFile,
                              Environment environment) {

    public String getMixinFile() {
        return mixinFile;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
