package org.bookmc.loader.impl.loader.candidate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.api.exception.LoaderException;
import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.impl.loader.JsonMetadataParserV0;
import org.bookmc.loader.impl.loader.container.ZipModContainer;
import org.bookmc.loader.shared.Constants;
import org.bookmc.loader.shared.zip.BetterZipFile;
import org.bookmc.loader.shared.zip.ZipUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Path;

public class ZipModCandidate implements ModCandidate {
    private final BetterZipFile zipFile;

    public ZipModCandidate(Path path) {
        try {
            if (!ZipUtils.isZipFile(path)) {
                throw new LoaderException("This candidate type is reserved for zip files!");
            }
            this.zipFile = new BetterZipFile(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public ZipModCandidate(BetterZipFile zipFile) {
        this.zipFile = zipFile;
    }

    private static final JsonParser parser = new JsonParser();

    @Override
    public boolean validate() {
        // To verify this as a "book mod" we check for the book.mod.json,
        return zipFile.getEntry(Constants.METADATA_FILE) != null;
    }

    @Override
    public void loadContainers0(AppendableURLClassLoader classLoader) {
        try {
            classLoader.addURL(zipFile.getFile().toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModContainer[] getContainers() {
        try {
            try (InputStream metadataFile = zipFile.getInputStream(zipFile.getEntry(Constants.METADATA_FILE))) {
                try (InputStreamReader reader = new InputStreamReader(metadataFile)) {
                    JsonElement object = parser.parse(reader);
                    if (object.isJsonArray()) {
                        return ZipModContainer.create(JsonMetadataParserV0.from(object.getAsJsonArray()), zipFile);
                    } else {
                        return new ModContainer[]{new ZipModContainer(JsonMetadataParserV0.from(object.getAsJsonObject()), zipFile)};
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModContainer[0];
    }
}
