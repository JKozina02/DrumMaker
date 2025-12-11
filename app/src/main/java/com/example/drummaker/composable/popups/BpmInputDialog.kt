package com.example.drummaker.composable.popups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BpmInputDialog(
    currentBpm: Int,
    onDismiss: () -> Unit,
    onBpmConfirm: (Int) -> Unit
) {
    var text by remember { mutableStateOf(currentBpm.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zmień BPM") },
        text = {
            Column {
                Text("Wprowadź nową wartość tempa.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.all { it.isDigit() } && newText.length <= 3) {
                            text = newText
                        }
                    },
                    label = { Text("BPM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newBpm = text.toIntOrNull()
                    if (newBpm != null) {
                        onBpmConfirm(newBpm)
                    }
                    onDismiss()
                },

                enabled = text.isNotBlank()
            ) {
                Text("Zatwierdź")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}