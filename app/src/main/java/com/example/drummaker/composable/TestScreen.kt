package com.example.drummaker.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drummaker.scripts.DrumViewModel

@Composable
fun TestScreen(viewModel: DrumViewModel) {
    val isReady by viewModel.isReady.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "DrumMaker - Test", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(40.dp))

        if (isReady) {
            Text("Silnik gotowy!", color = Color(0xFF008000), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.triggerSample(sampleId = 0) },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Odtwórz dźwięk (ID: 0)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { viewModel.play() }) {
                    Text("Play")
                }

                Button(onClick = { viewModel.pause() }) {
                    Text("Pause")
                }
            }

        } else {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ładowanie silnika audio...")
        }

        Spacer(modifier = Modifier.weight(1f))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Status: ${if (isReady) "✅ Gotowy" else "⌛ Oczekiwanie..."}")
                Text("Logcat filtr: DrumViewModel lub AudioEngine")
            }
        }
    }
}