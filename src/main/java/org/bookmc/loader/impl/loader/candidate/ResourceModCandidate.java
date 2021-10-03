package org.bookmc.loader.impl.loader.candidate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.api.config.LoaderConfig;
import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.impl.loader.JsonMetadataParserV0;
import org.bookmc.loader.impl.loader.container.ResourceModContainer;
import org.bookmc.loader.shared.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourceModCandidate implements ModCandidate {
    private final Logger LOGGER = LogManager.getLogger();
    private final JsonParser parser = new JsonParser();

    private final AppendableURLClassLoader classLoader;
    private final LoaderConfig config;

    public ResourceModCandidate(AppendableURLClassLoader classLoader, LoaderConfig config) {
        this.classLoader = classLoader;
        this.config = config;
    }

    @Override
    public boolean validate() {
        return classLoader.getResourceAsStream(Constants.METADATA_FILE) != null && !config.getOption("book.candidate.disableResourceSearching", true);
    }

    @Override
    public void loadContainers0(AppendableURLClassLoader classLoader) {

    }

    @Override
    public ModContainer[] getContainers() {
        try {
            try (InputStream metadataFile = classLoader.getResourceAsStream(Constants.METADATA_FILE)) {
                if (metadataFile == null) {
                    throw new UnsupportedOperationException("Something wen't wrong... This was called to early maybe?");
                }

                try (InputStreamReader reader = new InputStreamReader(metadataFile)) {
                    JsonElement object = parser.parse(reader);
                    if (object.isJsonArray()) {
                        return ResourceModContainer.create(JsonMetadataParserV0.from(object.getAsJsonArray()), classLoader);
                    } else {
                        if (!object.isJsonObject()) {
                            LOGGER.fatal("book-loader is about to fail! Please check your mod json format. Offendor: {}", Paths.get(Objects.requireNonNull(classLoader.getResource(Constants.METADATA_FILE)).toURI()).toString());
                        }
                        return new ModContainer[]{new ResourceModContainer(JsonMetadataParserV0.from(object.getAsJsonObject()), classLoader)};
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return new ModContainer[0];
    }
}
