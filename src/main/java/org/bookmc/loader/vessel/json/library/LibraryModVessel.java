package org.bookmc.loader.vessel.json.library;

import com.google.gson.JsonObject;
import org.bookmc.loader.vessel.json.JsonModVessel;

import java.io.File;

public class LibraryModVessel extends JsonModVessel {
    private final JsonObject object;

    public LibraryModVessel(JsonObject object, File file) {
        super(object, file);
        this.object = object;
    }

    @Override
    public String getEntrypoint() {
        return object.has("entrypoint") ? object.get("entrypoint").getAsString() : null;
    }
}
