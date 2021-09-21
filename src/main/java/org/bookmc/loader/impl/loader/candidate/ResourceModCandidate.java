package org.bookmc.loader.impl.loader.candidate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;
import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.impl.loader.JsonMetadataParserV0;
import org.bookmc.loader.impl.loader.container.ResourceModContainer;
import org.bookmc.loader.shared.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceModCandidate implements ModCandidate {
    private final JsonParser parser = new JsonParser();

    private final AbstractBookURLClassLoader classLoader;

    public ResourceModCandidate(AbstractBookURLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean validate() {
        return classLoader.getResourceAsStream(Constants.METADATA_FILE) != null;
    }

    @Override
    public void loadContainers0(AbstractBookURLClassLoader classLoader) {

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
                        return new ModContainer[]{new ResourceModContainer(JsonMetadataParserV0.from(object.getAsJsonObject()), classLoader)};
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModContainer[0];
    }
}
