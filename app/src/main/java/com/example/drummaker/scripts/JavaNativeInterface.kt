package com.example.drummaker.scripts

import android.content.res.AssetManager

object AudioEngineJNI {
    init {
        System.loadLibrary("drummaker")
    }

    external fun init(assetManager: AssetManager, sampleRate: Int, bufferSize: Int): Long
    external fun destroy(handle: Long)
    external fun play(handle: Long)
    external fun pause(handle: Long)
    external fun setBPM(handle: Long, bpm: Float)
    external fun getBPM(handle: Long): Float
    external fun loadWav(handle: Long, assetPath: String): Int
    external fun updateGrid(handle: Long, sampleId: Int, step: Int, isSet: Boolean)
    external fun trigger(handle: Long, sampleId: Int, velocity: Float)
}

