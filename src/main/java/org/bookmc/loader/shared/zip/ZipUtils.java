package org.bookmc.loader.shared.zip;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * For CPU efficiency ZipUtils reads the header
 * of the given file and decides whether it is a ZIP or not. If it
 * has decided it is a ZIP it stores the detected data into a cache
 * taking a string and returning a boolean. This data should be less
 * than storing the whole File object and throwing exceptions :)
 */
public class ZipUtils {
    private static final Map<String, Boolean> zipCache = new HashMap<>();

    // Taken from https://en.wikipedia.org/wiki/List_of_file_signatures
    private static final int[] ZIP_MAGIC = new int[]{0x50, 0x4B, 0x03, 0x04};

    public static boolean isZipFile(Path path) {
        if (zipCache.containsKey(path.toString())) {
            return zipCache.get(path.toString());
        }

        try (InputStream fileInputStream = Files.newInputStream(path)) {
            for (int magic : ZIP_MAGIC) {
                if (fileInputStream.read() != magic) {
                    zipCache.put(path.toString(), false);
                    return false;
                }
            }
        } catch (FileNotFoundException ignored) {
            zipCache.put(path.toString(), false);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            zipCache.put(path.toString(), false);
            return false;
        }

        zipCache.put(path.toString(), true);
        return true;
    }
}