package ui.smart_connect

import support.utility.runCommand

//adb device
//adb -s 4e7354af shell "ip addr show wlan0  | grep 'link/ether '| cut -d' ' -f6"

fun getSubnetDeviceList(): MutableList<NetworkDevices> {
    val deviceList = mutableListOf<NetworkDevices>()
    val command = COMMAND_ARP_A.runCommand() ?: ""
    val lines = command.replace(command.substring(0, command.indexOf("type", 0, true) + 6), "").split("\n")
    for (line in lines) {
        line.trim().extractOutputs()?.let { deviceList.add(it) }
    }
    return deviceList
}

fun getAdbDevices(): List<String> {
    val execOutput = COMMAND_ADB_DEVICES.runCommand() ?: ""
    val opList = execOutput.split("\n").toMutableList()
    if (opList.isNotEmpty()) {
        opList.removeAt(0)
    }
    return opList
        .filterNot { it == "\r" || it == "\n" || it.isEmpty() }
        .map { it.trimEnd().split("\t")[0] }
}

fun adbMacByIp(list: List<String>): MutableList<Pair<String, String>> {
    val ipMacList = mutableListOf<Pair<String, String>>()
    for (ip in list) {
        val execOutput = COMMAND_ADB_MAC_BY_IP(ip).runCommand()
        if (execOutput.success) {
            ipMacList.add(ip to execOutput.output.replace(":","-"))
        }
    }
    return ipMacList
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
    var name: String? = null,
    var isAdb: Boolean = false
)
