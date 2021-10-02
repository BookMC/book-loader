package org.bookmc.loader.impl.loader.container;

import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.ModMetadata;
import org.bookmc.loader.api.mod.metadata.ModResource;
import org.bookmc.loader.api.mod.state.ModState;

import java.util.ArrayList;
import java.util.List;

public class ResourceModContainer implements ModContainer {
    private final ModMetadata metadata;
    private final AppendableURLClassLoader classLoader;

    private ModState modState = ModState.UNKNOWN;

    public ResourceModContainer(ModMetadata metadata, AppendableURLClassLoader classLoader) {
        this.metadata = metadata;
        this.classLoader = classLoader;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    @Override
    public ModState getModState() {
        return modState;
    }

    @Override
    public void setModState(ModState state) {
        modState = state;
    }

    @Override
    public AppendableURLClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void setClassLoader(AppendableURLClassLoader classLoader) {
        // Impossible
    }

    @Override
    public ModResource createModResource(String name) {
        return () -> classLoader.getResourceAsStream(name);
    }

    public static ModContainer[] create(ModMetadata[] metadata, AppendableURLClassLoader classLoader) {
        List<ModContainer> modContainerList = new ArrayList<>();
        for (ModMetadata modMetadata : metadata) {
            modContainerList.add(new ResourceModContainer(modMetadata, classLoader));
        }
        return modContainerList.toArray(new ModContainer[0]);
    }
}
