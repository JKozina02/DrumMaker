package com.example.drummaker.composable

import SoundManager
import SoundPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.R
import com.example.drummaker.composable.reusable.BPMButton
import com.example.drummaker.composable.reusable.IconButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.composable.reusable.PlayerIconButton

@Composable
fun PadScreen(
    soundManager: SoundManager,
    soundPlayer: SoundPlayer
){
    var bpm by remember { mutableIntStateOf(soundPlayer.bPMGetter()) }
    soundPlayer.prepareAllSounds(soundManager)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            InnerShadowBox(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        IconButton(R.drawable.polygon_left, onClick = {
                            soundPlayer.delaySub()
                            bpm = soundPlayer.bPMGetter()
                        })
                        BPMButton(bpm){}
                        IconButton(R.drawable.polygon_right, onClick = {
                            soundPlayer.delayAdd()
                            bpm = soundPlayer.bPMGetter()
                        })
                    }
                    PlayerIconButton(R.drawable.rectangle, onClick = {soundPlayer.stopPlaying()})
                    PlayerIconButton(R.drawable.triangle, onClick = {soundPlayer.playSoundsLoop()})
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            InnerShadowBox { Text("PAD SCREEN TODO") }
        }
    }
}