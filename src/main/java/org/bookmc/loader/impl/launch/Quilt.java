package org.bookmc.loader.impl.launch;

import org.bookmc.loader.impl.launch.bootstrap.QuiltBootstrap;
import org.bookmc.loader.impl.launch.provider.ArgumentHandler;
import org.bookmc.loader.impl.launch.provider.DefaultGameProvider;
import org.bookmc.loader.impl.launch.transform.QuiltClassLoader;

import java.lang.reflect.Method;

public class Quilt {
    static QuiltClassLoader classLoader;

    public static void main(String[] args) throws Throwable {
        classLoader = new QuiltClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        ArgumentHandler handler = new ArgumentHandler(args);
        new Quilt().launch(args, handler);
    }

    private void launch(String[] args, ArgumentHandler handler) throws Throwable {
        Launcher.setGameProvider(new DefaultGameProvider(handler));
        String target = handler.get("target")
            .orElse(Launcher.getGameProvider().getLaunchTarget());

        QuiltBootstrap.plug();

        Class<?> clazz = Class.forName(target, false, classLoader);
        Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
    }
}
