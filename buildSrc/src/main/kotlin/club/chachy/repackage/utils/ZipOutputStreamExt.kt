package club.chachy.repackage.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * "Injects" a jar into another jar by taking it's contents and adding it into the other.
 *
 * @author ChachyDev
 */
fun ZipOutputStream.inject(file: ZipFile, newPackage: String?, remapper: Remapper) {
    val entries = file.entries().toList()

    entries.forEach {
        try {
            val repackagedEntry = newPackage?.let { p ->
                it.name.takeIf { name -> name.startsWith("META-INF") }?.let { name -> ZipEntry(name) }
                    ?: ZipEntry("$p/${it.name}")
            } ?: ZipEntry(it.name)

            // Enter the repackaged entry into the jar we're going to "inject" into
            putNextEntry(repackagedEntry)
            if (it.name.endsWith(".class")) {
                it.byteArray(file)?.let { b -> write(b.remap(remapper)) }
            } else {
                it.byteArray(file)?.let { b -> write(b) }
            }
            closeEntry()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun ByteArray.remap(remapper: Remapper): ByteArray {
    val writer = ClassWriter(0)
    val reader = ClassReader(this)
    reader.accept(ClassRemapper(writer, remapper), 0)
    val bytes = writer.toByteArray() ?: this
    return bytes
}