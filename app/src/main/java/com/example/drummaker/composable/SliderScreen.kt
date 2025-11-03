package com.example.drummaker.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.drummaker.composable.reusable.InnerShadowBox

@Composable
fun SliderScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row{
            InnerShadowBox { Text("SLIDER TODO") }
        }
    }
}