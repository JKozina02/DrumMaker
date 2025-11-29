package com.example.drummaker.composable

import SoundManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.composable.reusable.SequencerButton
import com.example.drummaker.ui.theme.Blue

@Composable
fun NoteScreen(soundManager: SoundManager) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column{
            InnerShadowBox(modifier = Modifier.fillMaxWidth()) {
                Text("NOTE SCREEN TODO")
            }

            Spacer(modifier = Modifier.size(10.dp))
                InnerShadowBox(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    Column() {
                        for (sound in soundManager.getAllSound())
                            Row() {
                                Text(sound.key)
                                SequencerButton(false, Blue) { soundManager.addSound(sound.key)}
                                if(soundManager.selectedSounds.contains(sound.key)) Text("Zaznaczony")
                            }

                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }