package club.chachy.repackage.utils

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

fun ZipEntry.byteArray(file: ZipFile) = file.getInputStream(this)
    ?.use { it.readBytes() }