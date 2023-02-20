package ui.smart_connect.ui.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState

val showLoader = mutableStateOf(false)

@Composable
fun loader(){
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