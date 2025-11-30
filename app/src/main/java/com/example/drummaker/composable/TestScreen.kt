package com.example.drummaker.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import com.example.drummaker.scripts.AudioEngineJNI

@Composable
fun TestScreen() {
    var handle by remember { mutableStateOf(0L) }
    var status by remember { mutableStateOf("Gotowy do testu ciszy") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DrumMaker - Test Ciszy",
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (handle == 0L) {
                    handle = AudioEngineJNI.init(44100, 192)
                    status = if (handle != 0L) {
                        "✅ Stream OK! Gra cisza (handle: $handle)"
                    } else {
                        "❌ Init błąd!"
                    }
                } else {
                    AudioEngineJNI.destroy(handle)
                    handle = 0L
                    status = "🛑 Zatrzymano"
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text(
                text = if (handle == 0L) "▶️ START CISZA" else "⏹️ STOP",
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = status,
                )
                Text(
                    text = "Logcat filtr: AudioEngine",
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Podnieś głośność → usłysz ciszę!",
        )
    }
}