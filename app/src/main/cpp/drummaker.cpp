#include <iostream>
#include <jni.h>
#include <vector>
#include <android/log.h>
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

class AudioEngine : public oboe::AudioStreamDataCallback {

private:
    std::vector<Sample> samples_;
    std::vector<Voice> voices_;
    std::shared_ptr<oboe::AudioStream> stream_;
    float bpm_;
    double phase_;
    float masterVolume_;
    std::mutex mutex_;
    int sampleRate;
    bool isStreamValid = false;

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
    bool isValid() const {
        return isStreamValid;
    }
};

AudioEngine::AudioEngine(int sampleRate, int bufferSize) {
    this->sampleRate = sampleRate;
    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(oboe::ChannelCount::Mono)
            ->setSampleRate(sampleRate)
            ->setFramesPerCallback(bufferSize)
            ->setDataCallback(this);

    oboe::Result result = builder.openStream(stream_);

    if (result == oboe::Result::OK && stream_ != nullptr) {
        stream_->requestStart();
        isStreamValid = true;
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Stream OK!");
    } else {
        isStreamValid = false;
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Stream FAILED: %s",
                            oboe::convertToText(result));
    }
}


oboe::DataCallbackResult AudioEngine::onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    //TODO replace test
    float *floatData = (float *) audioData;
    std::fill(floatData, floatData + numFrames, 0.0f);
    return oboe::DataCallbackResult::Continue;
}

AudioEngine::~AudioEngine() {
    if(stream_ && isStreamValid){
        stream_ -> requestStop();
        stream_ -> close();
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Stream closed!");
    }
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_init(JNIEnv *env, jobject thiz, jint sample_rate,
                                                       jint buffer_size) {
    AudioEngine* engine = new AudioEngine(sample_rate, buffer_size);
    if (!engine->isValid()) {
        delete engine;
        return 0L;
    }
    return reinterpret_cast<jlong>(engine);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_destroy(JNIEnv *env, jobject thiz, jlong handle) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    if (engine != nullptr){
        delete engine;
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "JNI: Destroy lunched!");
    }
    __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "JNI: engine is nullptr !");
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
extern "C"
JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_setBPM(JNIEnv *env, jobject thiz, jlong handle,
                                                         jfloat bpm) {
    // TODO: implement setBPM()
}