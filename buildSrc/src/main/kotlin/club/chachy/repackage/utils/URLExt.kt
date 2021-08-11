package club.chachy.repackage.utils

import java.io.File
import java.net.URL

fun URL.download(dest: File): File {
    if (dest.exists()) {
        return dest
    }

    dest.parentFile?.mkdirs()
    dest.createNewFile()

    openStream().use {
        dest.outputStream().use { os ->
            it.copyTo(os)
        }
    }

    return dest
}