package ui.smart_connect

import support.utility.runCommand

fun getDeviceList(): MutableList<NetworkDevices> {
    val deviceList = mutableListOf<NetworkDevices>()
    val command = COMMAND_ARP_A.runCommand() ?: ""
    val lines = command.replace(command.substring(0, command.indexOf("type", 0, true) + 6), "").split("\n")
    for (line in lines) {
        line.trim().extractOutputs()?.let { deviceList.add(it) }
    }
    return deviceList
}

fun String.extractOutputs(): NetworkDevices? {
    val split = this.replace("\\s{2,}".toRegex(), ":").split(":")
    return if (split.size == 3) {
        return NetworkDevices(split[0], split[1], split[2])
    } else {
        null
    }
}

data class NetworkDevices(
    val ip: String? = null,
    val mac: String? = null,
    val type: String? = null,
    var name: String? = null
)

const val COMMAND_ARP_A = "arp -a"
