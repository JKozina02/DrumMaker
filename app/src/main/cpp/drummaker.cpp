#include <iostream>
#include <jni.h>
#include <vector>
#include "oboe/Oboe.h"

struct Sample {
    std::vector<float> buffer;
    int length;
    float sampleRate;
};

struct Voice {
    bool active = false;
    int sampleId = -1;
    float position = 0.0f;
    float velocity = 0.0f;
};

class AudioEngine {

private:
    std::vector<Sample> samples_;
    std::vector<Voice> voices_;
    oboe::ManagedStream stream_;
    float bpm_;
    double phase_;
    float masterVolume_;
    std::mutex mutex_;

public:
    AudioEngine(int sampleRate, int bufferSize);
    ~AudioEngine();
    int addSample(const char* path);
    void trigger(int sampleId, float velocity);
    void setBPM(float bpm);
    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *oboeStream,
            void *audioData,
            int32_t numFrames);
};

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_init(JNIEnv *env, jobject thiz, jint sample_rate,
                                                       jint buffer_size) {
    // TODO: implement init()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_destroy(JNIEnv *env, jobject thiz, jlong handle) {
    // TODO: implement destroy()
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_loadWav(JNIEnv *env, jobject thiz, jlong handel,
                                                          jstring asset_path) {
    // TODO: implement loadWav()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_trigger(JNIEnv *env, jobject thiz, jlong handle,
                                                          jint sample_id, jfloat velocity) {
    // TODO: implement trigger()
}