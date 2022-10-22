@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import ui.smart_connect.*
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.swing.JOptionPane.showInputDialog
import javax.swing.JOptionPane.showMessageDialog


fun main() = application {
    val applicationState = remember { MyApplicationState() }

    for (window in applicationState.windows) {
        key(window) {
            ShowDeviceWindow(window)
        }
    }
}

val showLoader = mutableStateOf(false)
val showDialog: MutableState<Pair<Boolean, NetworkDevices?>> = mutableStateOf(Pair(false, null))
val selectedDevice: MutableState<NetworkDevices?> = mutableStateOf(null)

@Preview
@Composable
fun ShowDeviceWindow(window: MyWindowState) {
    var devicesList: MutableList<NetworkDevices> by remember { mutableStateOf(mutableListOf()) }
    var expanded by remember { mutableStateOf(false) }

    val windowState = WindowState(
        size = DpSize(500.dp, 500.dp),
    )
    Window(
        state = windowState,
        onCloseRequest = window::close,
        title = "SharkADB",
    ) {
        MenuBar {
            Menu("Action") {
                Item("Refresh", onClick = {
                    devicesList = initList()
                })
            }
        }
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(5.dp)
                    .border(BorderStroke(1.dp, Color(Color.Gray.value)))
            ) {
                items(items = devicesList,
                    key = {
                        it.hashCode()
                    }) { device ->
                    //var dname by mutableStateOf(null)

                    Row(
                        Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 6.dp).mouseClickable {
                            if (buttons.isSecondaryPressed) {
                                showDialog.value = true to device
                            }
                        },
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically).weight(1f),
                            text = device.mac ?: "",
                            style = textViewStyle,
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically).weight(1f),
                            text = device.ip ?: "",
                            style = textViewStyle
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically).weight(1f).clickable {
                                val input = showInputDialog("Device Name", device.name)
                                if (!input.isNullOrEmpty()) {
                                    device.name = input
                                    saveConfiguration(devicesList)
                                    devicesList.clear()
                                    devicesList = initList()
                                }
                            },
                            text = device.name ?: "?",
                            style = textViewStyle
                        )
                        Image(
                            painter = painterResource("ic_scrcpy.png"), "Merlin",
                            colorFilter = if (device.isAdb) null else ColorFilter.tint(Color.LightGray),
                            modifier = Modifier.size(20.dp)
                                .align(alignment = Alignment.CenterVertically)
                                .weight(0.2f)
                                .clickable(enabled = true, onClick = {
                                    device.ip?.let {
                                        if (scrcpyThread?.isAlive == true) {
                                            scrcpyThread?.interrupt()
                                            scrcpyThread = null
                                        }
                                        scrcpyThread = scrcpy(it) {

                                        }
                                    }
                                })
                        )
                        Image(
                            painter = painterResource(
                                if (device.isAdb) {
                                    "ic_more.png"
                                } else {
                                    "ic_refresh.png"
                                }
                            ),
                            contentDescription = "Refresh/More",
                            colorFilter = if (device.isAdb) null else ColorFilter.tint(Color.Gray),
                            modifier = Modifier.size(20.dp)
                                .align(alignment = Alignment.CenterVertically)
                                .weight(0.2f)
                                .clickable(enabled = true, onClick = {
                                    if (!device.isAdb) {
                                        selectedDevice.value = device
                                        if (!tryConnecting()) {
                                            showDialog.value = true to device
                                        }
                                    } else {
                                        showDialog.value = true to device
                                    }
                                })
                        )
                    }
                    Divider()
                }
            }
        }
        Dialog(
            title = "Options",
            state = DialogState(size = DpSize(Dp.Unspecified, Dp.Unspecified)),
            resizable = false,
            visible = showDialog.value.first,
            onCloseRequest = {
                showDialog.value = false to null
            },
            content = {
                connectedDialog()
            }
        )
        Dialog(
            title = "Loading..",
            state = DialogState(size = DpSize(Dp.Unspecified, Dp.Unspecified)),
            resizable = false,
            visible = showLoader.value,
            onCloseRequest = {},
            content = {
                CircularProgressIndicator(
                    modifier = Modifier.padding(5.dp)
                )
            }
        )
        //subnetScan()
        devicesList = initList()
    }
}

var scrcpyThread: Thread? = null

fun initList(): MutableList<NetworkDevices> {
    val activeDeviceList = getSubnetDeviceList()
    val adbMacList = adbMacByIp(getAdbDevices()).map { it.second }

    activeDeviceList.map { activeDevice ->
        activeDevice.isAdb = adbMacList.any() {
            it == activeDevice.mac
        }
    }

    for (loadedDevice in loadConfiguration() ?: listOf()) {
        val activeDevice = activeDeviceList.firstOrNull {
            it.mac == loadedDevice.mac
        }
        activeDevice?.name = loadedDevice.name
    }
    return activeDeviceList
}

val forceTcpIp = mutableStateOf(false)

@Composable
fun connectedDialog() {
    /*val forceTcpIp = remember { mutableStateOf(false) }*/

    Column(Modifier.padding(5.dp).size(200.dp, Dp.Unspecified)) {
        Row(modifier = Modifier.padding(5.dp)) {
            Image(
                modifier = Modifier.size(20.dp)
                    .align(alignment = Alignment.CenterVertically),
                painter = painterResource("ic_wifi_connect.png"),
                colorFilter = ColorFilter.tint(Color.Gray),
                contentDescription = null
            )
            Text(
                text = "Wifi Connect",
                Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 6.dp)
                    .align(alignment = Alignment.CenterVertically)
                    .clickable {
                        showLoader.value = true
                        showDialog.value.second?.ip?.let {
                            if (forceTcpIp.value) {
                                val tcpIpConnection = tcpipAdb(deviceName = it)
                                if (!tcpIpConnection.first) { //when tcpip fails
                                    showLoader.value = false
                                    showMessageDialog(null, tcpIpConnection.second)
                                }
                            }
                            val adbConnection = connectAdb(deviceName = it)
                            showLoader.value = false
                            showMessageDialog(null, adbConnection.second)
                        }
                    }
            )
            Checkbox(checked = forceTcpIp.value, onCheckedChange = {
                forceTcpIp.value = it
            })
        }
        Divider()
        Row(modifier = Modifier.padding(5.dp)) {
            Image(
                modifier = Modifier.size(20.dp)
                    .align(alignment = Alignment.CenterVertically),
                painter = painterResource("ic_unlink.png"),
                colorFilter = ColorFilter.tint(Color.Gray),
                contentDescription = null
            )
            Text(
                text = "Disconnect",
                Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 6.dp)
                    .clickable(showDialog.value.second?.isAdb ?: false) {
                        showLoader.value = true
                        val connectionOutput = disconnectAdb(showDialog.value.second?.ip)
                        showLoader.value = false
                        showMessageDialog(null, connectionOutput.second)
                    }
            )
        }
        Divider()
    }
}

fun tryConnecting(): Boolean {
    showLoader.value = true
    val device = selectedDevice.value!!
    val adbConnection = connectAdb(deviceName = device.ip!!)
    showLoader.value = false
    showMessageDialog(null, adbConnection.second)
    return adbConnection.first
}

fun subnetScan() {
    val e: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
    while (e.hasMoreElements()) {
        val n = e.nextElement() as NetworkInterface
        val ee: Enumeration<*> = n.inetAddresses
        while (ee.hasMoreElements()) {
            val nextInetAddress = ee.nextElement() as InetAddress

            for ((start,end) in 255f.getDividends(20)) {
                //println("Start: ${start} end: ${end}")
                giveAHug(nextInetAddress, start, end).start()
            }
            /*giveAHug(nextInetAddress, 0, 50).start()
            giveAHug(nextInetAddress, 51, 100).start()
            giveAHug(nextInetAddress, 101, 150).start()
            giveAHug(nextInetAddress, 151, 200).start()
            giveAHug(nextInetAddress, 201, 255).start()*/
        }
    }
}


val meowList = mutableListOf<String>()
fun giveAHug(
    nextInetAddress: InetAddress,
    start: Int = 0,
    upto: Int = 255
): Job {
    return CoroutineScope(Dispatchers.IO).async {
        val ip = nextInetAddress.hostAddress
        var macId = NetworkInterface.getByInetAddress(nextInetAddress).hardwareAddress
        val sip = ip.substring(0, ip.indexOf('.', ip.indexOf('.', ip.indexOf('.') + 1) + 1) + 1)
        try {
            for (it in start..upto) {
                val ipToTest = sip + it
                val online = ping(ipToTest)
                if (online?.toString() != "PT24H") {
                    meowList.add(ipToTest)
                    println("$ipToTest is online $online ${online?.toMillis()} macId: ${macId.toMac()}")
                }
            }
        } catch (e1: IOException) {
            println(sip)
        }
    }
}


fun ByteArray.toMac(): String {
    val sb = StringBuilder()
    for (i in 0 until this.size) {
        sb.append(java.lang.String.format("%02X%s", this.get(i), if (i < this.size - 1) "-" else ""))
    }
    return sb.toString()
}

fun ping(host: String?): Duration? {
    val startTime: Instant = Instant.now()
    try {
        val address = InetAddress.getByName(host)
        if (address.isReachable(1000)) {
            return Duration.between(startTime, Instant.now())
        }
    } catch (e: IOException) {
        // Host not available, nothing to do here
    }
    return Duration.ofDays(1)
}