package club.chachy.repackage

import club.chachy.repackage.constants.GUAVA
import club.chachy.repackage.constants.GUAVA_VERSION
import club.chachy.repackage.constants.REPACKAGE_PACKAGE
import club.chachy.repackage.constants.packagesToRePackage
import club.chachy.repackage.remapper.MixinRemapper
import club.chachy.repackage.stream.SafeZipOutputStream
import club.chachy.repackage.utils.copyFile
import club.chachy.repackage.utils.download
import club.chachy.repackage.utils.inject
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import java.io.File
import java.net.URL
import java.util.zip.ZipFile

/**
 * In order to provide a working instance of Mixin 0.8 we have to injected
 * guava into the Mixin jar and then apply a remapper on the relocated Guava
 * code to avoid conflicts with Minecraft
 *
 * @author ChachyDev
 */
abstract class MixinRepackage : TransformAction<TransformParameters.None> {
    // Injected by Gradle
    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val input = getInputArtifact().get().asFile

        val repackagedName = input.name.takeIf { it.endsWith(".jar") }?.let {
            it.replaceRange(it.length - 4, it.length, "-repackaged.jar")
        } ?: "${input.name}-repackaged"

        val output = outputs.file(repackagedName)
        // Look for mixin since that's what we're going to "inject" into
        if (input.name.startsWith("mixin") && input.extension == "jar") {
            SafeZipOutputStream(output.outputStream()).use { zos ->
                val remapper = MixinRemapper(packagesToRePackage, REPACKAGE_PACKAGE)
                zos.inject(locateGuava(), REPACKAGE_PACKAGE, remapper) // Inject guava
                zos.inject(ZipFile(input), null, remapper) // Inject Mixin
            }
        } else {
            // Just return the normal jar since we don't need to do anything
            copyFile(input, output)
        }
    }

    // Download guava or if it's already available use that
    private fun locateGuava(): ZipFile {
        val guava = guava().let {
            it.download(File(".gradle/repackage-downloads/${it.path}"))
        }

        return ZipFile(guava)
    }

    companion object {
        private fun guava(): URL {
            return URL(GUAVA.format(GUAVA_VERSION, GUAVA_VERSION))
        }
    }
}