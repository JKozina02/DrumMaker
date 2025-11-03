package com.example.drummaker.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.InnerShadowBox

@Composable
fun OptionScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row{
            InnerShadowBox { Text("OPTION TODO") }
        }
    }
}