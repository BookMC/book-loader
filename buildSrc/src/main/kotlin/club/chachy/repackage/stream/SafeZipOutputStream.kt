package club.chachy.repackage.stream

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SafeZipOutputStream(out: OutputStream) : ZipOutputStream(out) {
    private var currentWrite: ZipEntry? = null
    private val safetyCache = ArrayList<String>()

    override fun putNextEntry(e: ZipEntry) {
        currentWrite = e

        if (!safetyCache.contains(e.name)) {
            super.putNextEntry(e)
        }
    }

    override fun write(b: ByteArray) {
        if (currentWrite == null) {
            return super.write(b)
        }

        if (!safetyCache.contains(currentWrite!!.name)) {
            super.write(b)
        }
    }

    override fun closeEntry() {
        if (currentWrite == null) {
            return super.closeEntry()
        }

        if (!safetyCache.contains(currentWrite!!.name)) {
            safetyCache.add(currentWrite!!.name)
            super.closeEntry()
        }

        currentWrite = null
    }
}