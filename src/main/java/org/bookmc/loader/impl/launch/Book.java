package org.bookmc.loader.impl.launch;

import com.google.common.collect.Lists;
import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;
import org.bookmc.loader.api.config.LoaderConfig;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.exception.LoaderException;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.EntrypointType;
import org.bookmc.loader.api.mod.metadata.ModEntrypoint;
import org.bookmc.loader.api.service.GameDataService;
import org.bookmc.loader.api.service.PrelaunchService;
import org.bookmc.loader.impl.config.JVMLoaderConfig;
import org.bookmc.loader.impl.loader.BookLoaderImpl;
import org.bookmc.loader.impl.loader.BookParentClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class Book {
    private static boolean initialized = false;
    private static AbstractBookURLClassLoader classLoader;

    /**
     * Handles enviornment-common loading of the mod-loader.
     * Disallows usage of any environemnt other than CLIENT and SERVER.
     *
     * @param args        Arguments passed to the program
     * @param environment The environment to use globally
     */
    public static void launch(String[] args, GameEnvironment environment) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        checkLoaded();
        validateEnvironment(environment);
        LoaderConfig config = new JVMLoaderConfig();
        Map<String, String> arguments = createArgumentsMap(args);
        GameDataService service = findGameDataService();


        classLoader = new BookParentClassLoader();

        BookLoaderBase.INSTANCE = new BookLoaderImpl(service.getWorkingDirectory(arguments), environment, classLoader);
        BookLoaderBase.INSTANCE.preload(config);
        loadPrelaunchServices(arguments, environment);

        if (!config.hasOption("book.loader.skipEntrypoint")) {
            loadGame(service, args);
        }

        markLoaded();
    }

    private static void checkLoaded() {
        if (initialized) {
            throw new RuntimeException("You cannot initialise the loader more than once! Rethink your logic!");
        }
    }

    private static void markLoaded() {
        initialized = true;
    }

    /**
     * To make sure that we are being launched with an acceptable environment
     * to be used globally (checking for client-side and server-side mods).
     * We MUST decloare what we class as "acceptable" and check whether the environment
     * given at launch is acceptable.
     *
     * @param environment The environment that the loader was requested to load in
     */
    private static void validateEnvironment(GameEnvironment environment) {
        if (environment != GameEnvironment.SERVER && environment != GameEnvironment.CLIENT) {
            throw new RuntimeException("The provided environment (" + environment.name() + ") is not supported as a global environment!");
        }
    }

    /**
     * Converts arguments passed into a map of key-value
     * example of argument "--key argument --another_key argument"
     *
     * @param args The raw arguments passed by the {@link Book#launch(String[], GameEnvironment)} method
     * @return A key-value version of the argument data passed.
     */
    private static Map<String, String> createArgumentsMap(String[] args) {
        Map<String, String> argumentMap = new HashMap<>();
        int index = 0;
        for (String arg : args) {
            if (args.length > index + 1) {
                argumentMap.put(
                    arg.startsWith("--") ? arg.substring(2) : arg,
                    args.length < index ? null : args[index + 1]
                );
                index++;
            }
        }

        return argumentMap;
    }

    private static GameDataService findGameDataService() {
        return ServiceLoader.load(GameDataService.class)
            .findFirst()
            .orElseThrow(() -> new LoaderException("No game data service located, cannot launch!"));
    }

    private static void loadPrelaunchServices(Map<String, String> arguments, GameEnvironment environment) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (PrelaunchService service : findPrelaunchServices()) {
            service.prelaunch(arguments, environment);
        }
    }

    private static Iterable<PrelaunchService> findPrelaunchServices() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<PrelaunchService> serviceList = Lists.newArrayList(ServiceLoader.load(PrelaunchService.class).iterator());
        for (ModContainer container : BookLoaderBase.INSTANCE.getContainers().values()) {
            for (ModEntrypoint entrypoint : container.getMetadata().getEntrypoints()) {
                if (entrypoint.getEntrypointType() == EntrypointType.PRELAUNCH) {
                    Class<?> clazz = Class.forName(entrypoint.getEntryClass(), false, container.getClassLoader());
                    if (PrelaunchService.class.isAssignableFrom(clazz)) {
                        serviceList.add((PrelaunchService) clazz.getConstructor().newInstance());
                    }
                }
            }
        }

        return serviceList;
    }

    private static void loadGame(GameDataService service, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName(service.getGameEntrypoint(), false, classLoader);
        Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
    }
}
