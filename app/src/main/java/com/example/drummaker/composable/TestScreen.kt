package com.example.drummaker.composable

import android.content.res.AssetManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.drummaker.scripts.AudioEngineJNI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TestScreen() {
    val context = LocalContext.current
    var engineHandle by remember { mutableStateOf(0L) }
    var hihatSampleId by remember { mutableStateOf(-1) }
    var status by remember { mutableStateOf("Gotowy") }

    // --- Efekty do zarządzania cyklem życia silnika ---
    LaunchedEffect(Unit) {
        status = "Inicjalizowanie silnika..."
        withContext(Dispatchers.IO) {
            val handle = AudioEngineJNI.init(
                assetManager = context.assets,
                sampleRate = 48000,
                bufferSize = 192
            )

            if (handle != 0L) {
                // Uruchamiamy strumień od razu, aby był gotowy do gry
                AudioEngineJNI.play(handle)

                val loadedId = AudioEngineJNI.loadWav(handle, "hihatclose1.wav")

                withContext(Dispatchers.Main) {
                    engineHandle = handle
                    if (loadedId != -1) {
                        hihatSampleId = loadedId
                        status = "✅ Gotowy! Naciśnij przycisk, aby zagrać."
                    } else {
                        status = "❌ Błąd ładowania sampla!"
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    status = "❌ Błąd inicjalizacji silnika!"
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (engineHandle != 0L) {
                AudioEngineJNI.destroy(engineHandle)
            }
        }
    }

    // --- UI (Interfejs Użytkownika) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "DrumMaker - Test Samplera")
        Spacer(modifier = Modifier.height(48.dp))

        // Przycisk do wyzwalania dźwięku
        Button(
            onClick = {
                if (engineHandle != 0L && hihatSampleId != -1) {
                    // Wywołujemy trigger dla załadowanego sampla
                    AudioEngineJNI.trigger(engineHandle, hihatSampleId, 1.0f)
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f).height(64.dp),
            enabled = hihatSampleId != -1 // Przycisk aktywny tylko, gdy sampl jest załadowany
        ) {
            Text(text = "▶️ Zagraj Hi-Hat")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Karta ze statusem
        Card(modifier = Modifier.fillMaxWidth(0.9f)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Status: $status")
                Text(text = "Logcat filtr: AudioEngine")
            }
        }
    }
}
