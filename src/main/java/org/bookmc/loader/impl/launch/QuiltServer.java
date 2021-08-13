package org.bookmc.loader.impl.launch;

import org.bookmc.loader.api.vessel.environment.Environment;

public class QuiltServer {
    public static void main(String[] args) throws Throwable {
        Launcher.setEnvironment(Environment.SERVER);
        Quilt.main(args);
    }
}
