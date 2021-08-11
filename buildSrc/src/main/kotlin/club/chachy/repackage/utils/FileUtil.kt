package club.chachy.repackage.utils

import java.io.File

fun copyFile(`in`: File, out: File) = out.writeBytes(`in`.readBytes())
