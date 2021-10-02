package org.bookmc.loader.impl.transformer;

import org.bookmc.loader.api.classloader.transformers.BookTransformer;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.exception.LoaderException;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.EntrypointType;
import org.bookmc.loader.api.mod.metadata.ModEntrypoint;
import org.bookmc.loader.api.service.PrelaunchService;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class TransformerPrelaunchService implements PrelaunchService {
    @Override
    public void prelaunch(Map<String, String> arguments, GameEnvironment environment) {
        BookLoaderBase.INSTANCE.getContainers()
            .values()
            .forEach(container -> Arrays.stream(container.getMetadata()
                .getEntrypoints())
                .filter(this::checkEntrypoint)
                .forEach(entrypoint -> registerContainer(container, entrypoint))
            );
    }

    private boolean checkEntrypoint(ModEntrypoint entrypoint) {
        return entrypoint.getEntrypointType() == EntrypointType.TRANSFORMER;
    }

    private void registerContainer(ModContainer container, ModEntrypoint entrypoint) {
        try {
            if (container.getClassLoader() == null) {
                throw new IllegalStateException("Transformer classloader could not be found (This is most likely a loader issue or someone is messing with the loader)");
            }
            Class<?> clazz = Class.forName(entrypoint.getEntryClass(), false, container.getClassLoader());
            BookLoaderBase.INSTANCE.getTransformClassLoader().registerTransformer((BookTransformer) clazz.getConstructor().newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new LoaderException("Failed to load " + container.getMetadata().getId() + " (" + entrypoint.getEntryClass() + ")", e);
        }
    }
}
