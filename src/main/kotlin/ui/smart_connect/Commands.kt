package ui.smart_connect


const val COMMAND_ARP_A = "arp -a"
const val COMMAND_ADB_DEVICES = "adb devices"
fun COMMAND_ADB_MAC_BY_IP(deviceName:String): MutableList<String> {
    return mutableListOf<String>().apply {
        add("adb")
        add("-s")
        add(deviceName)
        add("shell")
        add("\"ip addr show wlan0  | grep 'link/ether '| cut -d' ' -f6\"")
    }
}