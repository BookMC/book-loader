package org.bookmc.loader.impl.loader.container;

import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.ModMetadata;
import org.bookmc.loader.api.mod.metadata.ModResource;
import org.bookmc.loader.api.mod.state.ModState;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipModContainer implements ModContainer {
    private final ModMetadata metadata;
    private final ZipFile zipFile;

    private ModState modState = ModState.UNKNOWN;
    private AppendableURLClassLoader classLoader;

    public ZipModContainer(ModMetadata metadata, ZipFile zipFile) {
        this.metadata = metadata;
        this.zipFile = zipFile;
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
        this.classLoader = classLoader;
    }

    @Override
    public ModResource createModResource(String name) {
        ZipEntry zipEntry = zipFile.getEntry(name);
        if (zipEntry == null) return null;
        return new ZipModResource(zipEntry, zipFile);
    }

    public static ModContainer[] create(ModMetadata[] metadata, ZipFile zipFile) {
        List<ModContainer> modContainerList = new ArrayList<>();
        for (ModMetadata modMetadata : metadata) {
            modContainerList.add(new ZipModContainer(modMetadata, zipFile));
        }
        return modContainerList.toArray(new ModContainer[0]);
    }
}
