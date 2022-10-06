package ui.smart_connect

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


val configurationFile: File
    get() {
        val configurationFile = File(File(System.getProperty("user.dir")), "config.ini")
        if (!configurationFile.exists()) {
            configurationFile.createNewFile()
        }
        return configurationFile
    }

fun loadConfiguration(): List<NetworkDevices>? {
    return Gson().fromJson<List<NetworkDevices>?>(configurationFile.readText(), object : TypeToken<List<NetworkDevices>>() {}.type)
}

fun saveConfiguration(devices: List<NetworkDevices>) {
    configurationFile.writeText(Gson().toJson(devices))
}