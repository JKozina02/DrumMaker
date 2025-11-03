package com.example.drummaker.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.ElevatedIconButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.ui.theme.BackgroundColor
import com.example.drummaker.R

@Composable
fun MainScreen() {

    var openScreen by remember { mutableStateOf("pad") }
    var page by remember { mutableIntStateOf(1) }

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
                .height(100.dp)
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )
        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                if(page == 1) {
                    ElevatedIconButton(
                        isHalved = true,
                        drawableId = R.drawable.arrow_down,
                        onClick = { page = 2 }
                    )
                    ElevatedIconButton(
                        openScreen == "slider",
                        drawableId = R.drawable.sliders,
                        onClick = { openScreen = "slider" }
                    )
                    ElevatedIconButton(
                        openScreen == "note",
                        drawableId = R.drawable.note,
                        onClick = { openScreen = "note" }
                    )
                    ElevatedIconButton(
                        openScreen == "pad",
                        drawableId = R.drawable.squares_four,
                        onClick = { openScreen = "pad" }
                    )
                }
                if (page == 2) {
                    ElevatedIconButton(
                        openScreen == "folder",
                        drawableId = R.drawable.gear,
                        onClick = { openScreen = "folder" }
                    )
                    ElevatedIconButton(
                        openScreen == "option",
                        drawableId = R.drawable.folder,
                        onClick = { openScreen = "option" }
                    )
                    ElevatedIconButton(
                        isHalved = true,
                        drawableId = R.drawable.arrowup,
                        onClick = { page = 1 }
                    )

                }
            }
        }
        Box(
            modifier = Modifier
            .fillMaxSize()
            .padding(10.dp))
        {
            when(openScreen){
                "pad" -> { PadScreen() }
                "note" -> { NoteScreen()}
                "slider" -> { SliderScreen() }
                "folder" -> { FolderScreen() }
                "option" -> { OptionScreen() }
            }
        }
    }
}