// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:OptIn(ExperimentalUnitApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import component.DropdownBox
import component.DropdownBuilder
import component.EditText
import component.EditTextBuilder
import support.utility.executeCommand
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable
@Preview
fun App() {

    MaterialTheme {
        Column(
            modifier = Modifier.verticalScroll(ScrollState(0)).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 13.dp),
                text = "JeyK",
                fontFamily = FontFamily.Cursive,
                fontSize = TextUnit(20f, TextUnitType.Sp)
            )
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(start = 60.dp, end = 60.dp, top = 13.dp, bottom = 25.dp)
            )

            Column {
                Row {
                    EditText(
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(10.dp).weight(1f),
                        editTextBuilder = EditTextBuilder(
                            hint = "xxx",
                            label = "First Name"
                        )
                    ) {
                        return@EditText it
                    }
                    EditText(
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(10.dp).weight(1f),
                        editTextBuilder = EditTextBuilder(
                            hint = "x",
                            label = "Last Name"
                        )
                    ) {
                        return@EditText it
                    }
                }
                Row {
                    Column(Modifier.weight(0.8f)) {
                        DropdownBox(
                            modifier = Modifier.padding(start = 10.dp, end = 5.dp),
                            dropdownBuilder = DropdownBuilder(list = listOf("One", "Two", "Three"), selectedItem = 0),
                        ) {
                            it
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        EditText(
                            singleLine = true,
                            Modifier.padding(start = 5.dp, end = 10.dp),
                            editTextBuilder = EditTextBuilder(
                                hint = "Country",
                            )
                        ) {
                            return@EditText it
                        }
                    }

                }
                EditText(
                    singleLine = true,
                    Modifier.fillMaxWidth().padding(10.dp),
                    editTextBuilder = EditTextBuilder(
                        hint = "Email",
                    )
                ) {
                    return@EditText it
                }
                EditText(
                    singleLine = false,
                    Modifier.fillMaxWidth().padding(10.dp),
                    editTextBuilder = EditTextBuilder(
                        hint = "Address",
                    )
                ) {
                    return@EditText it
                }
                Button(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    onClick = {
                        print(executeCommand("arp -a"))
                    }, content = {
                        Text("Scan")
                    })
            }
        }
    }
}

fun main() = application {
    val windowState = WindowState(
        size = DpSize(250.dp, 400.dp),
    )
    Window(
        state = windowState,
        resizable = false,
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
