package org.bookmc.loader.impl.loader.container;

import org.bookmc.loader.api.mod.metadata.ModResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipModResource implements ModResource {
    private InputStream inputStream;

    public ZipModResource(ZipEntry zipEntry, ZipFile zipFile) {
        try {
            this.inputStream = zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InputStream getResourceAsStream() {
        return inputStream;
    }
}
