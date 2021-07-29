package org.bookmc.loader.shared.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadUtils {
    public static File downloadFile(URL url, File dest) {
        if (dest.exists()) return dest;
        File parent = dest.getParentFile();

        if (parent != null) {
            parent.mkdirs();
            try {
                dest.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream stream = url.openStream()) {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];
                int read;

                while ((read = stream.read(buffer, 0, buffer.length)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }
        } catch (IOException e) {
            dest.delete();
            e.printStackTrace();
        }

        return dest;
    }
}
