package org.bookmc.loader;

import com.google.gson.JsonObject;
import org.bookmc.loader.vessel.json.JsonModVessel;

import java.io.File;

public class LibraryModVessel extends JsonModVessel {
    public LibraryModVessel(JsonObject object, File file) {
        super(object, file);
    }
}
