package com.example.drummaker.composable

import PlaySoundRepeatedlyButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.composable.reusable.SequencerButton
import com.example.drummaker.ui.theme.Blue
import com.example.drummaker.ui.theme.Red

@Composable
fun NoteScreen(){

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column{

                InnerShadowBox(modifier = Modifier.fillMaxWidth()) {

                    Text("NOTE SCREEN TODO")
                    PlaySoundRepeatedlyButton()
                }

            Spacer(modifier = Modifier.size(10.dp))

                Column() {
                    Row() {
                        SequencerButton(false, Blue) {}
                        SequencerButton(true, Blue) {}
                        SequencerButton(false, Blue) {}
                        SequencerButton(true, Blue) {}
                        SequencerButton(false, Blue) {}
                    }
                    Row(){
                        SequencerButton(true, Red) {}
                        SequencerButton(false, Red) {}
                        SequencerButton(true, Red) {}
                        SequencerButton(true, Red) {}
                        SequencerButton(true, Red) {}
                    }
                }
            }
        }
    }


@Composable
fun PlaySoundButton() {
    TODO("Not yet implemented")
}