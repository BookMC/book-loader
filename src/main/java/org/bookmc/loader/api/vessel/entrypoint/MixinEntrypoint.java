package org.bookmc.loader.api.vessel.entrypoint;

import org.bookmc.loader.api.vessel.environment.Environment;

public class MixinEntrypoint {
    private final String mixinFile;
    private final Environment environment;

    public MixinEntrypoint(String mixinFile, Environment environment) {
        this.mixinFile = mixinFile;
        this.environment = environment;
    }

    public String getMixinFile() {
        return mixinFile;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
