package com.example.drummaker.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.drummaker.composable.reusable.ElevatedIconButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.ui.theme.BackgroundColor
import com.example.drummaker.R
import com.example.drummaker.scripts.DrumViewModel

@Composable
fun MainScreen(viewModel: DrumViewModel) {
    val context = LocalContext.current
    var openScreen by remember { mutableStateOf("pad") }
    var page by remember { mutableIntStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background( color = BackgroundColor )
    ) {
        InnerShadowBox(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    bottom = 10.dp
                )
        ){
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if(page == 1) {
                    ElevatedIconButton(
                        openScreen == "pad",
                        drawableId = R.drawable.squares_four,
                        onClick = { openScreen = "pad" }
                    )
                    ElevatedIconButton(
                        openScreen == "note",
                        drawableId = R.drawable.note,
                        onClick = { openScreen = "note" }
                    )
                    ElevatedIconButton(
                        openScreen == "slider",
                        drawableId = R.drawable.sliders,
                        onClick = { openScreen = "slider" }
                    )
                    ElevatedIconButton(
                        isHalved = true,
                        drawableId = R.drawable.arrow_down,
                        onClick = { page = 2 }
                    )
                }
                if (page == 2) {
                    ElevatedIconButton(
                        isHalved = true,
                        drawableId = R.drawable.arrowup,
                        onClick = { page = 1 }
                    )
                    ElevatedIconButton(
                        openScreen == "option",
                        drawableId = R.drawable.folder,
                        onClick = { openScreen = "option" }
                    )
                    ElevatedIconButton(
                        openScreen == "folder",
                        drawableId = R.drawable.gear,
                        onClick = { openScreen = "folder" }
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
                "pad" -> { PadScreen(viewModel)}
                "note" -> { NoteScreen(viewModel)}
                "slider" -> { SliderScreen() }
                "folder" -> { FolderScreen() }
                "option" -> { OptionScreen() }
            }
        }
    }
}