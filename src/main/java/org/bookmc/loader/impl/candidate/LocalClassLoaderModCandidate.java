package org.bookmc.loader.impl.candidate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.vessel.JsonModVessel;
import org.bookmc.loader.shared.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LocalClassLoaderModCandidate implements ModCandidate {
    private final JsonParser parser = new JsonParser();

    private final List<ModVessel> vesselList = new ArrayList<>();

    @Override
    public ModVessel[] getVessels() {
        return vesselList.toArray(new ModVessel[0]);
    }

    @Override
    public boolean isAcceptable() {
        try (InputStream stream = this.getClass().getResourceAsStream(Constants.LOADER_JSON_FILE)) {
            if (stream == null) return false;

            try (InputStreamReader reader = new InputStreamReader(stream)) {
                JsonArray mods = parser.parse(reader).getAsJsonArray();

                for (int i = 0; i < mods.size(); i++) {
                    JsonObject mod = mods.get(i).getAsJsonObject();
                    // TODO: Use CodeSource to get the location
                    vesselList.add(new JsonModVessel(mod, null));
                }
                return true;
            }
        } catch (IOException ignored) {

        }

        return false;
    }

    @Override
    public void addToClasspath(ClassLoaderURLAppender appender) {

    }
}
