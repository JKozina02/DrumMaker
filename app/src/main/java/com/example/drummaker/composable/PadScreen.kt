package com.example.drummaker.composable

import SoundManager
import SoundPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.R
import com.example.drummaker.composable.reusable.BPMButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.composable.reusable.PlayerIconButton

@Composable
fun PadScreen(
    soundManager: SoundManager,
    soundPlayer: SoundPlayer
){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            InnerShadowBox(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row() {
                    Row(
                        Modifier.padding(5.dp)
                    ) {
                        PlayerIconButton(R.drawable.polygon_1, onClick = {})
                        BPMButton(120) { }
                        PlayerIconButton(R.drawable.polygon_2, onClick = {})
                    }
                    PlayerIconButton(R.drawable.rectangle_110, onClick = {})
                    PlayerIconButton(R.drawable.triangle, onClick = {})
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            InnerShadowBox { Text("PAD SCREEN TODO") }
        }
    }
}