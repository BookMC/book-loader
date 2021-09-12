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
            // In reality we hope we never get here, because if we do we're in for some big problems
            // sided mods and stuff won't work correctly and it'll most likely make everything burn so
            // instead of curling up in a ball and whining over the fact we couldn't detect the environment
            // we just attempt to shutdown the game in attempt to express that the game has not been launched
            // correctly, if this ever occurs PLEASE report it so I can either fix the issue or HELP you fix
            // the issue, 'ave a lovely day and thanks for reading this massive comment block for some reason :)
            throw new IllegalStateException("Failed to detect environment!");
        }
    },
    ANY {
        @Override
        public boolean allows(Environment environment) {
            return environment == CLIENT || environment == SERVER;
        }
    };

    public static Environment fromString(String environment) {
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
