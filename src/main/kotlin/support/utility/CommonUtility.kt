package support.utility

import org.apache.commons.io.IOUtils
import java.io.File
import java.nio.charset.StandardCharsets

fun isMac(): Boolean {
    return getProperty("os.name")?.contains("mac", true) == true
}

fun executeCommand(command: String): String {
    //"cmd", "/C", "dir"
    val inputStream = ProcessBuilder(shell().also { it.add(command) })
        .directory(File(System.getProperty("user.home")))
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .start().inputStream
    return IOUtils.toString(inputStream, StandardCharsets.UTF_8)
}

fun getProperty(command: String): String? {
    return System.getProperty(command)
}

private fun shell(): MutableList<String> {
    return if (isMac()) {
        mutableListOf("bash", "-c")
    } else {
        mutableListOf("CMD", "/C")
    }
}