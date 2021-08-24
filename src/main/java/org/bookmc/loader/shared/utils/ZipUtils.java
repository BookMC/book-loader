package org.bookmc.loader.shared.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * For memory efficiency and CPU efficiency ZipUtils reads the header
 * of the given file and decides whether it is a ZIP or not. If it
 * has decided it is a ZIP it stores the detected data into a cache
 * taking a string and returning a boolean. This data should be less
 * than storing the whole File object and throwing exceptions :)
 */
public class ZipUtils {
    private static final Map<String, Boolean> zipCache = new HashMap<>();

    // Taken from https://en.wikipedia.org/wiki/List_of_file_signatures
    private static final int[] ZIP_MAGIC = new int[]{0x50, 0x4B, 0x03, 0x04};

    public static boolean isZipFile(File file) {
        if (zipCache.containsKey(file.getAbsolutePath())) {
            return zipCache.get(file.getAbsolutePath());
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            for (int magic : ZIP_MAGIC) {
                if (fileInputStream.read() != magic) {
                    zipCache.put(file.getAbsolutePath(), false);
                    return false;
                }
            }
        } catch (FileNotFoundException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        zipCache.put(file.getAbsolutePath(), true);
        return true;
    }
}
