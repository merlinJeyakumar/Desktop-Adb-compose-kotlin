package ui.smart_connect


const val COMMAND_ARP_A = "arp -a"
const val COMMAND_ADB_DEVICES = "adb devices"
fun COMMAND_ADB_MAC_BY_IP(deviceName: String): MutableList<String> {
    return mutableListOf<String>().apply {
        add("adb")
        add("-s")
        add(deviceName)
        add("shell")
        add("\"ip addr show wlan0  | grep 'link/ether '| cut -d' ' -f6\"")
    }
}

fun COMMAND_ADB_WIFI_CONNECT(deviceName: String): MutableList<String> {
    return mutableListOf<String>().apply {
        add("adb")
        add("connect")
        add(deviceName)
    }
}

fun COMMAND_ADB_DISCONNECT(deviceName: String?): MutableList<String> {
    return mutableListOf<String>().apply {
        add("adb")
        add("disconnect")
        deviceName?.let {
            add(it)
        }
    }
}

fun COMMAND_ADB_TCPIP_RESTART(deviceName: String?): MutableList<String> {
    return mutableListOf<String>().apply {
        add("adb")
        deviceName?.let {
            add("-s")
            add(deviceName)
        }
        add("tcpip")
        add("5555")
    }
}