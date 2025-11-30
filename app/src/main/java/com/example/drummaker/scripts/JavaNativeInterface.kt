package com.example.drummaker.scripts

object AudioEngineJNI {
    external fun init(sampleRate: Int, bufferSize: Int): Long
    external fun destroy(handle: Long)
    external fun loadWav(handel: Long, assetPath: String): Int
    external fun trigger(handle: Long, sampleId: Int, velocity: Float)
    external fun setBPM(handle: Long, bpm: Float)

    init {
        System.loadLibrary("audioengine")
    }
}

