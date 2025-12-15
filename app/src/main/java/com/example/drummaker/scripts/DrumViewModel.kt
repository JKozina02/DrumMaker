package com.example.drummaker.scripts

import android.app.Application
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drummaker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Sample private constructor(
    val name: String,
    val type: String,
    val url: String,
    @DrawableRes val typeImage: Int,
    val isPicked: Boolean = false
) {
    companion object {
        operator fun invoke(
            name: String,
            type: String,
            url: String,
            isPicked: Boolean = false
        ): Sample {
            val imageRes = when (type) {
                "bass" -> R.drawable.bass
                "snare" -> R.drawable.snare
                "cymbals" -> R.drawable.cymbals
                else -> R.drawable.unknown
            }
            return Sample(name, type, url, imageRes, isPicked)
        }
    }
}
private val initialAvailableSamples: List<Sample> = listOf(
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

private const val MAX_SAMPLES = 6
private const val MAX_STEPS = 64
private const val MIN_STEPS = 4
private const val INITIAL_STEPS = 16
private const val INITIAL_BPM = 120
private const val MIN_BPM = 40
private const val MAX_BPM = 260


class DrumViewModel(application: Application) : AndroidViewModel(application) {
    private var engineHandle: Long = 0L
    private val _availableSamples = MutableStateFlow(initialAvailableSamples)
    val availableSamples = _availableSamples.asStateFlow()

    private val _loadedSamples = MutableStateFlow<Map<Int, Sample>>(emptyMap())
    val loadedSamples = _loadedSamples.asStateFlow()

    private val _sequencerGrid = MutableStateFlow(
        Array(MAX_SAMPLES) { BooleanArray(MAX_STEPS) { false } }
    )
    val sequencerGrid = _sequencerGrid.asStateFlow()

    private val _bpm = MutableStateFlow(INITIAL_BPM)
    val bpm = _bpm.asStateFlow()

    private val _patternLength = MutableStateFlow(INITIAL_STEPS)
    val patternLength = _patternLength.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            engineHandle = AudioEngineJNI.init(
                assetManager = application.assets,
                sampleRate = 48000,
                bufferSize = 192
            )
            if (engineHandle == 0L) {
                Log.e("DrumViewModel", "Błąd inicjalizacji silnika audio.")
            } else {
                AudioEngineJNI.setBPM(engineHandle, INITIAL_BPM.toFloat())
                AudioEngineJNI.setPatternLength(engineHandle, INITIAL_STEPS)

            }
        }
    }

    fun addSample(sample: Sample) {
        if (engineHandle == 0L) return

        viewModelScope.launch(Dispatchers.IO) {
            val sampleId = AudioEngineJNI.loadWav(engineHandle, sample.url)
            if (sampleId != -1) {
                _loadedSamples.update { currentMap ->
                    currentMap.toMutableMap().also { it[sampleId] = sample }
                }
                updateAvailableSamples(sample.url, true)
                Log.i("DrumViewModel", "Dodano sampel '${sample.name}' do slotu ID: $sampleId")
            } else {
                Log.w("DrumViewModel", "Nie udało się dodać sampla '${sample.name}'. Brak wolnych slotów.")
            }
        }
    }

    fun removeSample(sampleId: Int) {
        if (engineHandle == 0L) return

        val sampleToRemove = _loadedSamples.value[sampleId] ?: return

        viewModelScope.launch(Dispatchers.IO) {
            AudioEngineJNI.removeSample(engineHandle, sampleId)

            _loadedSamples.update { currentMap ->
                currentMap.toMutableMap().also { it.remove(sampleId) }
            }

            updateAvailableSamples(sampleToRemove.url, false)

            _sequencerGrid.update { currentGrid ->
                val newGrid = currentGrid.map { it.clone() }.toTypedArray()
                newGrid[sampleId] = BooleanArray(MAX_STEPS) { false }
                newGrid
            }
            Log.i("DrumViewModel", "Usunięto sampel '${sampleToRemove.name}' ze slotu ID: $sampleId i wyczyszczono jego sekwencję.")
        }
    }

    private fun updateAvailableSamples(url: String, isPicked: Boolean) {
        _availableSamples.update { currentList ->
            currentList.map {
                if (it.url == url) {
                    Sample(it.name, it.type, it.url, isPicked)
                } else {
                    it
                }
            }
        }
    }

    fun toggleSequencerStep(sampleId: Int, step: Int) {
        if (engineHandle == 0L) return

        if (step >= _patternLength.value) return

        val newGrid = _sequencerGrid.value.map { it.clone() }.toTypedArray()
        val currentStepState = newGrid.getOrNull(sampleId)?.getOrNull(step) ?: return
        val newStepState = !currentStepState
        newGrid[sampleId][step] = newStepState

        _sequencerGrid.value = newGrid

        viewModelScope.launch(Dispatchers.IO) {
            AudioEngineJNI.updateGrid(engineHandle, sampleId, step, newStepState)
        }
    }

    fun setPatternLength(newLength: Int) {
        if (engineHandle == 0L) return

        val clampedLength = newLength.coerceIn(MIN_STEPS, MAX_STEPS)
        _patternLength.value = clampedLength

        viewModelScope.launch(Dispatchers.IO) {
            AudioEngineJNI.setPatternLength(engineHandle, clampedLength)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (engineHandle != 0L) {
            AudioEngineJNI.destroy(engineHandle)
            engineHandle = 0L
        }
    }

    fun play() {
        if (engineHandle != 0L) AudioEngineJNI.play(engineHandle)
    }

    fun pause() {
        if (engineHandle != 0L) AudioEngineJNI.pause(engineHandle)
    }

    fun setBPM(newBpm: Int) {
        if (engineHandle == 0L) return

        val clampedBpm = newBpm.coerceIn(40, 300)

        _bpm.value = clampedBpm

        viewModelScope.launch(Dispatchers.IO) {
            AudioEngineJNI.setBPM(engineHandle, clampedBpm.toFloat())
        }
    }
}