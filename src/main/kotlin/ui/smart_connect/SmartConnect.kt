import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ui.smart_connect.MyApplicationState
import ui.smart_connect.MyWindowState

fun main() = application {
    val applicationState = remember { MyApplicationState() }

    for (window in applicationState.windows) {
        key(window) {
            render(window)
        }
    }
}

@Preview
@Composable
fun render(window: MyWindowState) {
    val windowState = WindowState(
        size = DpSize(250.dp, 400.dp),
    )
    Window(
        state = windowState,
        onCloseRequest = window::close,
        title = window.title,
        ) {
        MenuBar {
            Menu("File") {
                Item("New window", onClick = window.openNewWindow)
                Item("Exsit", onClick = window.exit)
            }
        }
    }
}

@Preview
@Composable
fun meow(window: MyWindowState) {
    val windowState = WindowState(
        size = DpSize(250.dp, 400.dp),
    )
    Window(
        state = windowState,
        onCloseRequest = window::close,
        title = "meow",
        ) {
        MenuBar {
            Menu("File") {
                Item("New window", onClick = window.openNewWindow)
                Item("Exsit", onClick = window.exit)
            }
        }
    }
}
