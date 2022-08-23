@file:OptIn(ExperimentalUnitApi::class)

package component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun EditText(
    singleLine: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth().padding(10.dp),
    editTextBuilder: EditTextBuilder = EditTextBuilder(),
    valueChange: (EditTextBuilder) -> EditTextBuilder,
) {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = text,
        placeholder = {
            Text(editTextBuilder.hint,fontSize = TextUnit(14f, TextUnitType.Sp))
        },
        onValueChange = {
            editTextBuilder.input = it
            val build = valueChange(editTextBuilder)
            text = build.input ?: it
            isError = build.isError
        },
        isError = isError,
        label = { Text(editTextBuilder.label,fontSize = TextUnit(14f, TextUnitType.Sp)) },
        textStyle = TextStyle(fontSize = TextUnit(14f, TextUnitType.Sp)),
        singleLine = singleLine
    )
}

@Preview
@Composable
private fun PreviewTextBox() {
    EditText(
        editTextBuilder = EditTextBuilder(
            hint = "Example Hint"
        )
    ) { input ->
        return@EditText input.also {
            it.isError = input.input == "jeyk"
        }
    }
}

data class EditTextBuilder(
    var hint: String = "",
    var input: String? = "",
    var isError: Boolean = false,
    var label: String = ""
)