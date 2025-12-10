package com.example.drummaker.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drummaker.R
import com.example.drummaker.composable.reusable.ElevatedTypeIcon
import com.example.drummaker.composable.reusable.IconButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.scripts.DrumViewModel
import com.example.drummaker.ui.theme.ElevatedBackgroundColor
import com.example.drummaker.ui.theme.ElevatedBorderUnselected
import com.example.drummaker.ui.theme.TextColor

private const val MAX_SAMPLES = 8
@Composable
fun NoteScreen(viewModel: DrumViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InnerShadowBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Search Bar Area", modifier = Modifier.align(Alignment.Center))
                }

                InnerShadowBox(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(availableSamples) { sample ->
                            SampleView(sample)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                InnerShadowBox(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(MAX_SAMPLES) { index ->
                            LoadedSampleSlotView(index = index + 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadedSampleSlotView(index: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = ElevatedBackgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = "  Slot $index", color = TextColor)
    }
}

@Composable
fun SampleView(sample: Sample) {
    Box(
        modifier = Modifier
            .background(
                color = ElevatedBackgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(5.dp),
                color = ElevatedBorderUnselected
            )
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedTypeIcon(sample.typeImage)
            Text(sample.name, color = TextColor)
            Box(Modifier.padding(end = 10.dp)){
                IconButton(R.drawable.triangle, size = 25, onClick = {})
            }
        }
    }
}

data class Sample private constructor(
    val name: String,
    val type: String,
    val url: String,
    @DrawableRes val typeImage: Int
) {
    companion object {
        operator fun invoke(name: String, type: String, url: String): Sample {
            val imageRes = when (type) {
                "bass" -> R.drawable.bass
                "snare" -> R.drawable.snare
                "cymbals" -> R.drawable.cymbals
                else -> R.drawable.unknown
            }
            return Sample(name, type, url, imageRes)
        }
    }
}

val availableSamples: List<Sample> = listOf(
    Sample("Kick", "bass", "kick-808.wav"),
    Sample("Snare", "snare", "snare-808.wav"),
    Sample("Clap", "snare", "clap-808.wav"),
    Sample("Hi-hat", "cymbals", "hihat-808.wav"),
    Sample("Open-hat", "cymbals", "openhat-808.wav"),
    Sample("Tom", "snare", "tom-808.wav"),
    Sample("Cowbell", "cowbell", "cowbell-808.wav"),
    Sample("Crash", "cymbals", "crash-808.wav"),
    Sample("Perc", "snare", "perc-808.wav")
)
