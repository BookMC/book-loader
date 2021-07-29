package org.bookmc.loader.api.vessel.environment;

import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.Locale;

public enum Environment {
    CLIENT {
        @Override
        public boolean allows(Environment environment) {
            return environment == CLIENT || environment == ANY;
        }
    },
    SERVER {
        @Override
        public boolean allows(Environment environment) {
            return environment == CLIENT || environment == ANY;
        }
    },
    UNKNOWN {
        @Override
        public boolean allows(Environment environment) {
            throw new IllegalStateException("Failed to detect environment!");
        }
    },
    ANY {
        @Override
        public boolean allows(Environment environment) {
            return environment == CLIENT || environment == SERVER;
        }
    };

    public static Environment getEnvironment(String environment) {
        if (environment.equals("*")) return ANY;
        if (environment.toLowerCase(Locale.ROOT).equals("client")) return CLIENT;
        if (environment.toLowerCase(Locale.ROOT).equals("server")) return SERVER;
        return UNKNOWN;
    }

    public static MixinEnvironment.Side toMixin(Environment environment) {
        if (environment == CLIENT) return MixinEnvironment.Side.CLIENT;
        if (environment == SERVER) return MixinEnvironment.Side.SERVER;
        return MixinEnvironment.Side.UNKNOWN;
    }

    public abstract boolean allows(Environment environment);
}
