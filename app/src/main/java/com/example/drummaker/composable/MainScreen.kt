package com.example.drummaker.composable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.ui.theme.BackgroundColor
import com.example.drummaker.ui.theme.DrumMakerTheme

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = BackgroundColor
            )
    ) {
        InnerShadowBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )
        ){

        }
        InnerShadowBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ){

        }
    }
}