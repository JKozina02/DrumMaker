package com.example.drummaker.composable

import PlaySoundRepeatedlyButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.InnerShadowBox

@Composable
fun NoteScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column{
            InnerShadowBox {
                Text("NOTE SCREEN TODO")
                PlaySoundRepeatedlyButton()
            }
            Spacer(modifier = Modifier.size(10.dp))
            InnerShadowBox {  }
        }
    }
}

@Composable
fun PlaySoundButton() {
    TODO("Not yet implemented")
}