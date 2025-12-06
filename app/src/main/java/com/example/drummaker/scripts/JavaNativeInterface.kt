package com.example.drummaker.scripts

object AudioEngineJNI {
    init {
        System.loadLibrary("drummaker")
    }

    external fun init(sampleRate: Int, bufferSize: Int): Long
    external fun destroy(handle: Long)
    external fun play(handle: Long)
    external fun pause(handle: Long)
    external fun setBPM(handle: Long, bpm: Float)
    external fun getBPM(handle: Long): Float
    external fun loadWav(handle: Long, assetPath: String): Int
    external fun trigger(handle: Long, sampleId: Int, velocity: Float)
    external fun updateGrid(handle: Long, sampleId: Int, step: Int, isSet: Boolean)
}

