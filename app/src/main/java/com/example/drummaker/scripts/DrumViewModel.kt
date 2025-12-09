package com.example.drummaker.scripts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DrumViewModel(application: Application) : AndroidViewModel(application) {
    private var engineHandle: Long = 0L

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            engineHandle = AudioEngineJNI.init(
                assetManager = application.assets,
                sampleRate = 48000,
                bufferSize = 192
            )
            if (engineHandle != 0L) {
                _isReady.value = true
            } else {
                Log.e("DrumViewModel", "Błąd inicjalizacji silnika audio.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (engineHandle != 0L) {
            AudioEngineJNI.destroy(engineHandle)
            engineHandle = 0L
        }
    }

    fun triggerSample(sampleId: Int) {
        if (engineHandle != 0L && _isReady.value) {
            AudioEngineJNI.trigger(engineHandle, sampleId, 1.0f)
        }
    }

    fun play() {
        if (engineHandle != 0L) {
            AudioEngineJNI.play(engineHandle)
        }
    }

    fun pause() {
        if (engineHandle != 0L) {
            AudioEngineJNI.pause(engineHandle)
        }
    }

    fun setBpm(bpm: Float) {
        if (engineHandle != 0L) {
            AudioEngineJNI.setBPM(engineHandle, bpm)
        }
    }

    fun updateGrid(sampleId: Int, step: Int, isSet: Boolean) {
        if (engineHandle != 0L) {
            AudioEngineJNI.updateGrid(engineHandle, sampleId, step, isSet)
        }
    }
}