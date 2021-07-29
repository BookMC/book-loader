package org.bookmc.loader.shared.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public class ZipUtils {
    private static final Map<File, Boolean> zipCache = new HashMap<>();

    public static boolean isZipFile(File file) {
        if (zipCache.containsKey(file)) {
            return zipCache.get(file);
        }

        try {
            new ZipFile(file);
            zipCache.put(file, true);
            return true;
        } catch (IOException e) {
            zipCache.put(file, false);
            return false;
        }
    }
}
