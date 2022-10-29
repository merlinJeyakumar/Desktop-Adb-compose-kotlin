package ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import ui.smart_connect.window.MyWindowState

@Composable
fun ApplicationScope.BasicWindow(
    state: MyWindowState
) = Window(
    onCloseRequest = state::close,
    title = state.title,

) {
    MenuBar {
        Menu("File") {
            Item("New window", onClick = state.openNewWindow)
            Item("Exit", onClick = state.exit)
        }
    }
}