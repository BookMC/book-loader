package org.bookmc.loader.impl.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bookmc.loader.api.mod.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;

public class JsonMetadataParserV0 {
    public static ModMetadata[] from(JsonArray array) {
        List<ModMetadata> containers = new ArrayList<>();
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                containers.add(from(element.getAsJsonObject()));
            }
        }
        return containers.toArray(new ModMetadata[0]);
    }

    public static ModMetadata from(JsonObject object) {
        int schemaVersion = object.has("schemaVersion") ? object.get("schemaVersion").getAsInt() : 0;
        if (schemaVersion != 0) {
            throw new IllegalStateException("The metadata parser (V0) does not support this version! (" + schemaVersion + ")");
        }
        return new JsonMetadataV0(object);
    }
}