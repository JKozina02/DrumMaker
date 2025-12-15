package com.example.drummaker.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import com.example.drummaker.composable.reusable.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.scripts.DrumViewModel
import com.example.drummaker.ui.theme.TextColor
import com.example.drummaker.R
import androidx.compose.runtime.getValue

private val validBeatDivisions = listOf(2, 4, 8, 16)

@Composable
fun OptionScreen(viewModel: DrumViewModel) {
    val patternLength by viewModel.patternLength.collectAsState()
    val beatDivision = 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        InnerShadowBox {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Pattern",
                    color = TextColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))

                SettingsRow(label = "Pattern Length") {
                    ValueControl(
                        value = patternLength,
                        onIncrement = {
                            val newLength = patternLength + 4
                            viewModel.setPatternLength(newLength)
                        },
                        onDecrement = {
                            val newLength = patternLength - 4
                            viewModel.setPatternLength(newLength)
                        }
                    )
                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                SettingsRow(label = "Beat Division") {
//                    ValueControl(
//                        value = beatDivision,
//                        onIncrement = { /* TODO: Logika w ViewModel */ },
//                        onDecrement = { /* TODO: Logika w ViewModel */ }
//                    )
//                }
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextColor, fontSize = 18.sp)
        content()
    }
}
@Composable
private fun ValueControl(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(R.drawable.polygon_left, size = 30, onClick = onDecrement)
        Text(
            text = "$value",
            color = TextColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        IconButton(R.drawable.polygon_right, size = 30, onClick = onIncrement)
    }
}