package com.example.drummaker.composable

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drummaker.R
import com.example.drummaker.composable.reusable.ElevatedTypeIcon
import com.example.drummaker.composable.reusable.IconButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.scripts.DrumViewModel
import com.example.drummaker.scripts.Sample
import com.example.drummaker.ui.theme.ElevatedBackgroundColor
import com.example.drummaker.ui.theme.ElevatedBorderUnselected
import com.example.drummaker.ui.theme.TextColor

private const val MAX_SAMPLES = 6

@Composable
fun NoteScreen(viewModel: DrumViewModel) {
    val availableSamples by viewModel.availableSamples.collectAsState()
    val loadedSamples by viewModel.loadedSamples.collectAsState()

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
                        items(availableSamples.filter { !it.isPicked }) { sample ->
                            SampleView(
                                sample = sample,
                                onClick = { viewModel.addSample(sample) }
                            )
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
                            val sampleInSlot = loadedSamples[index]
                            LoadedSampleSlotView(
                                index = index + 1,
                                sample = sampleInSlot,
                                onRemoveClick = { viewModel.removeSample(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadedSampleSlotView(index: Int, sample: Sample?, onRemoveClick: () -> Unit) {
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sample?.name ?: "$index",
                color = TextColor,
                fontSize = 26.sp
            )
            if (sample != null) {
                IconButton(R.drawable.xmlid_933_, size = 25, onClick = onRemoveClick)
            }
        }
    }
}

@Composable
fun SampleView(sample: Sample, onClick: () -> Unit) {
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
            Box(Modifier.padding(end = 10.dp)) {
                IconButton(R.drawable.plus, size = 25, onClick = onClick)
            }
        }
    }
}