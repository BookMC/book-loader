package org.bookmc.loader.impl.launch;

import org.bookmc.loader.api.vessel.environment.Environment;

public class QuiltClient {
    public static void main(String[] args) throws Throwable {
        Launcher.setEnvironment(Environment.CLIENT);
        Quilt.main(args);
    }
}
