package ui.smart_connect.window

import androidx.compose.runtime.mutableStateListOf

class MyApplicationState {
    val windows = mutableStateListOf<MyWindowState>()

    init {
        windows += MyWindowState("Smart Connect", "initial")
    }

    fun openNewWindow(id: String, title: String = "Smart Connect") {
        windows += MyWindowState(title, id)
    }

    fun exit() {
        windows.clear()
    }

    fun MyWindowState(
        id: String,
        title: String
    ) = MyWindowState(
        title,
        openNewWindow = {
            openNewWindow(id)
        },
        exit = ::exit,
        windows::remove
    )
}