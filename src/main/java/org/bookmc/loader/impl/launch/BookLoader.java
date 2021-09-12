package org.bookmc.loader.impl.launch;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.provider.GameProvider;
import org.bookmc.loader.api.vessel.environment.Environment;
import org.bookmc.loader.impl.bootstrap.QuiltBootstrap;
import org.bookmc.loader.impl.provider.ArgumentHandler;
import org.bookmc.loader.impl.classloader.QuiltClassLoader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;

public class BookLoader {
    private static final Logger LOGGER = LogManager.getLogger(BookLoader.class);
    public static Environment environment;
    static QuiltClassLoader classLoader;

    public static void main(String[] args) throws Throwable {
        classLoader = new QuiltClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        new BookLoader().launch(args);
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        if (BookLoader.environment != null) {
            throw new IllegalStateException("You cannot change the environment once it has been set, rethink your logic!");
        }

        BookLoader.environment = environment;
    }

    private void launch(String[] args) throws Throwable {
        List<GameProvider> providers = getGameProviders();
        ArgumentHandler handler = new ArgumentHandler(args);
        if (providers.size() > 0) {
            GameProvider provider = providers.get(0);

            provider.load(handler);
            LOGGER.info("Using provided GameProvider {}, index in providers ({})", provider.getClass().getName(), providers.indexOf(provider));

            BookLauncher.setGameProvider(provider);
        }

        String launchTarget = BookLauncher.getGameProvider().getLaunchTarget();

        LOGGER.info("Plugging in QuiltBootstrap!");
        QuiltBootstrap.plug();

        LOGGER.info("Searching for launch target provided by GameProvider ({})", launchTarget);
        Class<?> clazz = Class.forName(launchTarget, false, classLoader);
        LOGGER.info("Found launch target {}, searching for entrypoint method (main)", launchTarget);
        Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
        LOGGER.info("Found launch target's entrypoint method (main), preparing to invoke!");
        mainMethod.invoke(null, (Object) args);
    }

    private List<GameProvider> getGameProviders() {
        ServiceLoader<GameProvider> providers = ServiceLoader.load(GameProvider.class, classLoader);
        List<GameProvider> providerList = Lists.newArrayList(providers);
        if (providerList.size() == 0) {
            LOGGER.error("Failed to find any game providers, the game will crash soon!");
        } else {
            LOGGER.info("Successfully detected {} game providers", providerList.size());
        }
        return providerList;
    }
}
