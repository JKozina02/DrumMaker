package com.example.drummaker.composable

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.drummaker.R
import com.example.drummaker.composable.reusable.BPMButton
import com.example.drummaker.composable.reusable.IconButton
import com.example.drummaker.composable.reusable.InnerShadowBox
import com.example.drummaker.composable.reusable.PlayerIconButton
import com.example.drummaker.composable.reusable.SequencerButton
import com.example.drummaker.composable.popups.BpmInputDialog
import com.example.drummaker.scripts.DrumViewModel
import com.example.drummaker.ui.theme.Blue

private const val NUM_STEPS = 16
private const val MAX_SAMPLES = 6
@Composable
fun PadScreen(viewModel: DrumViewModel) {
    val trackColors = listOf(
        Color(0xFFFF0000),
        Color(0xFFFF3C00),
        Color(0xFF0062FF),
        Color(0xff00F6FF),
        Color(0xFFFFD900),
        Color(0xFFB700FF),
        Color(0xFF0DFF00),
        Color(0xFFFF00B2)
    )
    val gridState by viewModel.sequencerGrid.collectAsState()
    val loadedSamples by viewModel.loadedSamples.collectAsState()

    val currentBPM by viewModel.bpm.collectAsState()
    var showBpmDialog by remember { mutableStateOf(false) }

    if (showBpmDialog) {
        BpmInputDialog(
            currentBpm = currentBPM,
            onDismiss = { showBpmDialog = false },
            onBpmConfirm = { newBpm ->
                viewModel.setBPM(newBpm)
                showBpmDialog = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            InnerShadowBox(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        IconButton(R.drawable.polygon_left, onClick = {
                            viewModel.setBPM(currentBPM - 1)
                        })
                        BPMButton(currentBPM) {showBpmDialog = true}
                        IconButton(R.drawable.polygon_right, onClick = {
                            viewModel.setBPM(currentBPM + 1)
                        })
                    }
                    PlayerIconButton(R.drawable.rectangle, onClick = { viewModel.pause() })
                    PlayerIconButton(R.drawable.triangle, onClick = { viewModel.play() })
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            InnerShadowBox(modifier = Modifier.fillMaxSize()) {
                val scrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(scrollState)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (sampleId in 0 until MAX_SAMPLES) {
                            if (loadedSamples.containsKey(sampleId)) {
                                SequencerRow(
                                    gridState = gridState[sampleId],
                                    litColor = trackColors.getOrElse(sampleId) { Blue }, // Pobieramy kolor z listy
                                    onPadClick = { stepIndex ->
                                        viewModel.toggleSequencerStep(sampleId, stepIndex)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SequencerRow(gridState: BooleanArray, onPadClick: (Int) -> Unit, litColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        gridState.forEachIndexed { stepIndex, isSet ->
            SequencerButton(
                isSelected = isSet,
                clickedColor = litColor,
                onClick = { onPadClick(stepIndex) }
            )
        }
    }
}
