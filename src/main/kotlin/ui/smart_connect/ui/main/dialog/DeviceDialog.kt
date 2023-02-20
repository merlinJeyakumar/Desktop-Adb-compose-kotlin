package ui.smart_connect.ui.main.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import connectedDialog
import showDialog
import ui.smart_connect.ui.dialog.showLoader

@Composable
fun deviceDialog(){
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

}