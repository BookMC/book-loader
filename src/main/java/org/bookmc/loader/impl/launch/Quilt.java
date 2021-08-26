package org.bookmc.loader.impl.launch;

import com.google.common.collect.Lists;
import org.bookmc.loader.impl.launch.bootstrap.QuiltBootstrap;
import org.bookmc.loader.impl.launch.provider.ArgumentHandler;
import org.bookmc.loader.impl.launch.provider.DefaultGameProvider;
import org.bookmc.loader.impl.launch.provider.GameProvider;
import org.bookmc.loader.impl.launch.transform.QuiltClassLoader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;

public class Quilt {
    static QuiltClassLoader classLoader;

    public static void main(String[] args) throws Throwable {
        classLoader = new QuiltClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        ArgumentHandler handler = new ArgumentHandler(args);
        new Quilt().launch(args, handler);
    }

    private void launch(String[] args, ArgumentHandler handler) throws Throwable {
        List<GameProvider> providers = getGameProviders();
        for (GameProvider provider : providers) {
            if (providers.size() > 1 && provider instanceof DefaultGameProvider) {
                continue;
            }
            provider.load(handler);
            Launcher.setGameProvider(provider);
            break;
        }
        String target = handler.get("target")
            .orElse(Launcher.getGameProvider().getLaunchTarget());

        QuiltBootstrap.plug();

        Class<?> clazz = Class.forName(target, false, classLoader);
        Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
    }

    private List<GameProvider> getGameProviders() {
        ServiceLoader<GameProvider> providers = ServiceLoader.load(GameProvider.class, classLoader);
        return Lists.newArrayList(providers);
    }
}
