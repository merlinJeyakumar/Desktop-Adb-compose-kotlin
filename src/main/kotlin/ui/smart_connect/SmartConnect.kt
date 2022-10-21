@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
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
import ui.smart_connect.*
import javax.swing.JOptionPane.showInputDialog
import javax.swing.plaf.ProgressBarUI

fun main() = application {
    val applicationState = remember { MyApplicationState() }

    for (window in applicationState.windows) {
        key(window) {
            ShowDeviceWindow(window)
        }
    }
}

val showLoader = mutableStateOf(false)

@Preview
@Composable
fun ShowDeviceWindow(window: MyWindowState) {
    var devicesList: MutableList<NetworkDevices> by remember { mutableStateOf(mutableListOf()) }
    var expanded by remember { mutableStateOf(false) }

    val showDialog = mutableStateOf(false)
    val windowState = WindowState(
        size = DpSize(500.dp, 500.dp),
    )
    Window(
        state = windowState,
        onCloseRequest = window::close,
        title = "Network Devices",
    ) {
        MenuBar {
            Menu("Action") {
                Item("Refresh", onClick = {
                    devicesList = getSubnetDeviceList()
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
                                expanded = true
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
                                device.name = input
                                saveConfiguration(devicesList)
                                devicesList.clear()
                                devicesList = initList()
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

                                })
                        )
                        Image(
                            painter = painterResource("ic_more.png"),
                            contentDescription = "Merlin",
                            colorFilter = if (device.isAdb) null else ColorFilter.tint(Color.Gray),
                            modifier = Modifier.size(20.dp)
                                .align(alignment = Alignment.CenterVertically)
                                .weight(0.2f)
                                .clickable(enabled = true, onClick = {
                                    showDialog.value = true
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
            visible = showDialog.value,
            onCloseRequest = {
                showDialog.value = false
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
        devicesList = initList()
    }
}

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

@Composable
fun connectedDialog() {
    val forceTcpIp = remember { mutableStateOf(false) }

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
                        if (forceTcpIp.value) {
                            //cmd
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
                    .clickable {
                        //adb disconnect <device name>
                    }
            )
        }
        Divider()
    }
}

