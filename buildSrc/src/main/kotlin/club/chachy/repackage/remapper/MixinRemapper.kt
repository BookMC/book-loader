package club.chachy.repackage.remapper

import org.objectweb.asm.commons.Remapper

class MixinRemapper(private val packages: List<String>, private val newPackage: String) : Remapper() {
    /**
     * Remap class names via [Remapper]
     */
    override fun map(internalName: String?): String? {
        if (internalName == null) return null
        for (`package` in packages) {
            if (internalName.startsWith(`package`)) {
                return "$newPackage/$internalName"
            }
        }

        return super.map(internalName)
    }
}