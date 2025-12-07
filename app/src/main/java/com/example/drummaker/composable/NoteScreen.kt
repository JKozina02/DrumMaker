package com.example.drummaker.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun NoteScreen() {

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
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }