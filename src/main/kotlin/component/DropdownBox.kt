@file:OptIn(ExperimentalUnitApi::class)

package component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*

@Composable
fun DropdownBox(
    modifier: Modifier = Modifier.fillMaxWidth().padding(10.dp),
    dropdownBuilder: DropdownBuilder = DropdownBuilder(),
    valueChange: (DropdownBuilder) -> DropdownBuilder
) {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Column {
        OutlinedTextField(
            modifier = modifier,
            value = text,
            placeholder = {
                Text(dropdownBuilder.hint,fontSize = TextUnit(14f, TextUnitType.Sp))
            },
            onValueChange = {
                dropdownBuilder.input = it
                val build = valueChange(dropdownBuilder)
                text = build.input ?: it
                isError = build.isError
            },
            isError = isError,
            label = { Text(dropdownBuilder.label, fontSize = TextUnit(14f, TextUnitType.Sp)) },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            },
            readOnly = true,
            singleLine = true,
            textStyle = TextStyle(fontSize = TextUnit(14f, TextUnitType.Sp))
        )
        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for ((index, item) in dropdownBuilder.list.withIndex()) {
                DropdownMenuItem(onClick = {
                    dropdownBuilder.selectedItem = index
                    text = item
                    valueChange(dropdownBuilder)
                    expanded = false
                }) {
                    Text(text = item)
                }
            }
        }
    }
    text = dropdownBuilder.list[dropdownBuilder.selectedItem]
}

@Preview
@Composable
private fun PreviewTextBox() {
    DropdownBox(
        dropdownBuilder = DropdownBuilder(
            hint = "Example Hint"
        )
    ) { input ->
        return@DropdownBox input.also {
            it.isError = input.input == "jeyk"
        }
    }
}

data class DropdownBuilder(
    var hint: String = "",
    var input: String? = "",
    var isError: Boolean = false,
    var label: String = "",
    var list: List<String> = mutableListOf(),
    var selectedItem: Int = 0
)