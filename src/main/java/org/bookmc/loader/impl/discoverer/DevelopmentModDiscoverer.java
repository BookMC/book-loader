package org.bookmc.loader.impl.discoverer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.utils.ClassUtils;
import org.bookmc.loader.impl.vessel.JsonModVessel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DevelopmentModDiscoverer implements MinecraftModDiscoverer {

    @Override
    public void discover(File[] files) {
        // Only run in a deobfuscated environment
        if (ClassUtils.isClassAvailable("net.minecraft.client.Minecraft")) {
            // (Files are useless to us)
            try (InputStream inputStream = this.getClass().getResourceAsStream("/book.mod.json")) {
                if (inputStream != null) {
                    try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                        JsonArray mods = parser.parse(inputStreamReader).getAsJsonArray();

                        for (int i = 0; i < mods.size(); i++) {
                            JsonObject mod = mods.get(i).getAsJsonObject();
                            if (!Loader.getModVesselsMap().containsKey(mod.get("id").getAsString())) {
                                Loader.registerVessel(new JsonModVessel(mod, new File("there_is_no_mod_to_see")));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isFilesRequired() {
        return false;
    }
}
