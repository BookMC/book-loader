package org.bookmc.loader.impl.loader;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;
import org.bookmc.loader.api.classloader.transformers.BookTransformer;
import org.bookmc.loader.api.config.LoaderConfig;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.exception.LoaderException;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.*;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.api.mod.resolution.ResolverService;
import org.bookmc.loader.impl.loader.classloader.ModClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

public class BookLoaderImpl extends BookLoaderBase {
    private final Path workingDirectory;
    private final GameEnvironment globalEnvironment;
    private final AbstractBookURLClassLoader globalClassLoader;

    private boolean separateClassLoader;

    public BookLoaderImpl(Path workingDirectory, GameEnvironment globalEnvironment, AbstractBookURLClassLoader parentClassLoader) {
        super(workingDirectory.resolve("mods"), workingDirectory.resolve("config"));
        this.workingDirectory = workingDirectory;
        this.globalEnvironment = globalEnvironment;
        this.globalClassLoader = parentClassLoader;
    }

    @Override
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public GameEnvironment getGlobalEnvironment() {
        return globalEnvironment;
    }

    @Override
    public AbstractBookURLClassLoader getGlobalClassLoader() {
        return globalClassLoader;
    }

    @Override
    public void preload(LoaderConfig config) {
        separateClassLoader = config.getOption("separateClassLoader", true);
        for (ResolverService service : ServiceLoader.load(ResolverService.class)) {
            for (ModResolver resolver : service.getModResolvers()) {
                addResolver(resolver);
            }
        }

        loadCandidates();
        fixDependencyClassLoaders();
    }

    @Override
    public void load() throws LoaderException {
        for (ModContainer container : containers.values()) {
            if (!loaded.contains(container.getMetadata().getId())) {
                load0(container);
                loaded.add(container.getMetadata().getId());
            }
        }
    }

    private void load0(ModContainer container) throws LoaderException {
        ModReliance[] reliances = getAllReliances(container.getMetadata());
        for (ModReliance reliance : reliances) {
            ModContainer relianceContainer = containers.get(reliance.getId());
            load0(relianceContainer);
        }
        for (ModEntrypoint entrypoint : container.getMetadata().getEntrypoints()) {
            if (entrypoint.getEntrypointType() == EntrypointType.MAIN) {
                try {
                    Class<?> clazz = Class.forName(entrypoint.getEntryClass(), false, container.getClassLoader());
                    Method entryMethod = clazz.getDeclaredMethod(entrypoint.getEntryMethod());
                    entryMethod.invoke(null);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new LoaderException("Failed to load " + container.getMetadata().getId() + " (" + entrypoint.getEntryClass() + ")", e);
                }
            }
            if (entrypoint.getEntrypointType() == EntrypointType.TRANSFORMER) {
                try {
                    Class<?> clazz = Class.forName(entrypoint.getEntryClass(), false, container.getClassLoader());
                    globalClassLoader.registerTransformer((BookTransformer) clazz.getConstructor().newInstance());
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    throw new LoaderException("Failed to load " + container.getMetadata().getId() + " (" + entrypoint.getEntryClass() + ")", e);
                }
            }
        }
    }

    private void loadCandidates() {
        for (ModResolver resolver : resolvers) {
            ModCandidate[] candidates = resolver.resolveMods();
            for (ModCandidate candidate : candidates) {
                if (candidate.validate()) {
                    AbstractBookURLClassLoader classLoader = separateClassLoader ? new ModClassLoader(globalClassLoader) : globalClassLoader;
                    candidate.loadContainers(classLoader);
                    for (ModContainer container : candidate.getContainers()) {
                        String key = container.getMetadata().getId();
                        if (containers.containsKey(key)) {
                            throw new IllegalStateException(key + " has already been registered as a container!");
                        }
                        containers.put(key, container);
                    }
                }
            }
        }
    }

    /**
     * In order for dependencies to have access to their dependant component we must
     * put them onto the same classloader. However we must ALSO put anything that depends
     * on that onto the classloader. If single classloader operations are enabled we don't use
     * this functionality as it is simply useless.
     */
    private void fixDependencyClassLoaders() {
        for (ModContainer container : containers.values()) {
            ModReliance[] reliances = getAllReliances(container.getMetadata());
            for (ModReliance reliance : reliances) {
                String relianceId = reliance.getId();
                if (!containers.containsKey(relianceId)) {
                    throwMissingDependency(container, reliance);
                }

                ModContainer relianceContainer = containers.get(relianceId);
                ModVersion resolvedRelianceVersion = relianceContainer.getMetadata().getVersion();
                ModVersion requestedVersion = reliance.getRequestedVersion();
                int compareInt = reliance.getVersionIndicator().getCompareInt();
                int compareTo = requestedVersion == null ? compareInt : resolvedRelianceVersion.compareTo(requestedVersion);

                if (compareTo != compareInt) {
                    throwIncorrectVersionDependency(container, reliance, relianceContainer, compareTo);
                }
            }
        }
    }

    private ModReliance[] getAllReliances(ModMetadata metadata) {
        List<ModReliance> relianceList = new ArrayList<>();
        relianceList.addAll(Arrays.asList(metadata.getDependencies()));
        relianceList.addAll(Arrays.asList(metadata.getSuggestions()));
        return relianceList.toArray(new ModReliance[0]);
    }

    private void throwMissingDependency(ModContainer container, ModReliance reliance) {
        String id = container.getMetadata().getId();
        String relianceId = reliance.getId();
        throw new RuntimeException("Failed to start book-loader! " + id + " is missing " + relianceId + ". Please install it to continue!");
    }

    private void throwIncorrectVersionDependency(ModContainer container, ModReliance reliance, ModContainer presentReliance, int compareTo) {
        String name = container.getMetadata().getName();
        String id = container.getMetadata().getId();
        String relianceId = reliance.getId();
        if (!id.equals(name)) {
            id = id + " (" + name + ")";
        }
        String version = reliance.getRequestedVersion().getVersion();
        version = version == null ? "*" : version;

        String presentRelianceId = presentReliance.getMetadata().getId();
        String presentRelianceName = presentReliance.getMetadata().getName();
        if (!presentRelianceId.equals(presentRelianceName)) {
            presentRelianceId = presentRelianceId + " (" + presentRelianceName + ")";
        }
        String presentRelianceVersion = presentReliance.getMetadata().getVersion().getVersion();

        throw new RuntimeException("Failed to start book-loader! " + id + " requested for " + relianceId + "(" + version + ") and it must be " + reliance.getVersionIndicator().name() + " but received " + presentRelianceId + " (" + presentRelianceVersion + ") with " + VersionIndicator.fromInt(compareTo));
    }
}