package org.bookmc.loader.api.classloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.classloader.transformers.BookTransformer;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.ModEntrypoint;
import org.bookmc.loader.api.mod.metadata.ModMetadata;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TransformableURLClassLoader extends AbstractBookURLClassLoader {
    protected final List<BookTransformer> transformers = new ArrayList<>();
    protected final List<String> transformationExclusions = new ArrayList<>();

    private final Logger LOGGER = LogManager.getLogger();

    public TransformableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public byte[] modifyResolvedBytes(String name, byte[] bytes) {
        return !transformationExclusions.contains(name) ? transformClass(name, bytes) : bytes;
    }

    public byte[] transformClass(String name, byte[] clazz) {
        for (BookTransformer transformer : transformers) {
            try {
                clazz = transformer.proposeTransformation(name, clazz);
            } catch (Throwable t) {
                String transformerClass = transformer.getClass().getName();
                LOGGER.error("An error occured from {} ({}) when trying to transform {}", transformerClass, getTransformerOwner(transformerClass), name);
                t.printStackTrace();
            }
        }
        return clazz;
    }

    public void registerTransformer(BookTransformer transformer) {
        transformers.add(transformer);
    }

    private String getTransformerOwner(String transformer) {
        return BookLoaderBase.INSTANCE.getContainers()
            .values()
            .stream()
            .filter(c -> Arrays.stream(c.getMetadata().getEntrypoints())
                .map(ModEntrypoint::getEntryClass)
                .collect(Collectors.toSet())
                .contains(transformer)
            ).findFirst()
            .map(ModContainer::getMetadata)
            .map(ModMetadata::getName)
            .orElse("unknown");
    }
}
