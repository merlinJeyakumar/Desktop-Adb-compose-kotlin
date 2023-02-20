package support.utility

import org.apache.commons.io.IOUtils
import ui.smart_connect.shell.ExecutionOutput
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

fun isMac(): Boolean {
    return getProperty("os.name")?.contains("mac", true) == true
}

fun is64Bit(): Boolean {
    return getProperty("os.arch")?.contains("64", true) == true
}

@Deprecated("use runCommand insteadof")
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
fun List<String>.runCommand(
    workingDir: File = File(System.getProperty("user.home")),
    timeoutAmount: Long = 10,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): ExecutionOutput {
    return try {
        val processBuilder = ProcessBuilder(this)
            .directory(workingDir)
            .redirectErrorStream(true)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        processBuilder.waitFor(timeoutAmount, timeoutUnit)
        val output = processBuilder.inputStream.bufferedReader().readText().trimStart().trimEnd()
        ExecutionOutput(processBuilder.exitValue() != 1, output)
    } catch (e: Exception) {
        e.printStackTrace()
        ExecutionOutput(false, e.localizedMessage)
    }
}

fun String.runCommand(
    workingDir: File = File(System.getProperty("user.home")),
    timeoutAmount: Long = 2,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String? = runCatching {
    ProcessBuilder("\\s".toRegex().split(this))
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
        .inputStream.bufferedReader().readText()
}.onFailure { it.printStackTrace() }.getOrNull()

private fun shell(): MutableList<String> {
    return if (isMac()) {
        mutableListOf("bash", "-c")
    } else {
        mutableListOf("CMD", "/C")
    }
}