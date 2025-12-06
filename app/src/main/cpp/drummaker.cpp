#include <iostream>
#include <jni.h>
#include <vector>
#include <android/log.h>
#include "oboe/Oboe.h"
#include <cmath>

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

const int NUM_STEPS = 16;
const int MAX_SAMPLES = 8;
const int POLYPHONY = 16;

class AudioEngine : public oboe::AudioStreamDataCallback {

private:
    std::vector<Sample> samples_;
    std::vector<Voice> voices_;
    std::shared_ptr<oboe::AudioStream> stream_;
    bool isStreamValid = false;
    std::mutex mutex_;
    int sampleRate;

    float bpm_ = 120.0f;
    int currentStep_ = 0;
    double samplesPerStep_ = 0.0;
    double nextStepSample_ = 0.0;
    double currentSample_ = 0.0;
    bool grid_[MAX_SAMPLES][NUM_STEPS] = {{false}};
    bool isPlaying_ = false;


public:
    AudioEngine(int sampleRate, int bufferSize);
    ~AudioEngine();

    int addSample(const char* path);
    void play();
    void pause();
    float getBPM() const;
    void setBPM(float bpm);
    void updateGrid(int sampleId, int step, bool isSet);
    void trigger(int sampleId, float velocity = 1.0f);

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
    voices_.resize(POLYPHONY);
    setBPM(bpm_);
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
        isStreamValid = true;
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Stream OK!");
    } else {
        isStreamValid = false;
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Stream FAILED: %s",
                            oboe::convertToText(result));
    }
}


oboe::DataCallbackResult AudioEngine::onAudioReady(oboe::AudioStream* oboeStream, void* audioData, int32_t numFrames) {
    float* outputBuffer = static_cast<float*>(audioData);

    std::fill(outputBuffer, outputBuffer + numFrames, 0.0f);
    std::lock_guard<std::mutex> lock(mutex_);

    if (!isPlaying_) {
        return oboe::DataCallbackResult::Continue;
    }

    for (int i = 0; i < numFrames; ++i) {
        if (currentSample_ >= nextStepSample_) {
            currentStep_ = (currentStep_ + 1) % NUM_STEPS;

            for (int sampleIdx = 0; sampleIdx < MAX_SAMPLES; ++sampleIdx) {
                if (grid_[sampleIdx][currentStep_]) {
                    trigger(sampleIdx);
                }
            }
            nextStepSample_ += samplesPerStep_;
        }

        for (Voice& voice : voices_) {
            if (voice.active) {
                int readPosition = static_cast<int>(floor(voice.position));
                outputBuffer[i] += samples_[voice.sampleId].buffer[readPosition] * voice.velocity;

                voice.position += 1.0f;

                if (voice.position >= samples_[voice.sampleId].length) {
                    voice.active = false;
                }
            }
        }
        currentSample_++;
    }
    return oboe::DataCallbackResult::Continue;
}

AudioEngine::~AudioEngine() {
    if(stream_ && isStreamValid){
        stream_ -> requestStop();
        stream_ -> close();
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Stream closed!");
    }
}

void AudioEngine::play() {
    std::lock_guard<std::mutex> lock(mutex_);
    if(isValid() && !isPlaying_){
        isPlaying_ = true;
        currentSample_ = 0.0;
        currentStep_ = NUM_STEPS - 1;
        nextStepSample_ = 0.0;
        stream_->requestStart();
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Sequencer started!");
    }
    else __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Sequencer did not start!");
}

void AudioEngine::pause() {
    std::lock_guard<std::mutex> lock(mutex_);
    if(isPlaying_){
        isPlaying_ = false;
        this->stream_->requestStop();
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Sequencer Stopped!");
    }
    else{
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Sequencer did not Stop!");
    }
}

void AudioEngine::trigger(int sampleId, float velocity) {
    if (sampleId < 0 || sampleId >= samples_.size()) return;

    for (auto& voice : voices_) {
        if (!voice.active) {
            voice.sampleId = sampleId;
            voice.position = 0.0f;
            voice.velocity = velocity;
            voice.active = true;
            break;
        }
    }
}

void AudioEngine::updateGrid(int sampleId, int step, bool isSet) {

    if (sampleId < 0 || sampleId >= MAX_SAMPLES || step < 0 || step >= NUM_STEPS) return;
    std::lock_guard<std::mutex> lock(mutex_);
    grid_[sampleId][step] = isSet;
}

void AudioEngine::setBPM(float bpm) {
    std::lock_guard<std::mutex> lock(mutex_);
    bpm_ = bpm;
    samplesPerStep_ = (60.0 / bpm_) * sampleRate / 4.0;
}

float AudioEngine::getBPM() const {
    return this->bpm_;
}


extern "C" JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_play(JNIEnv *env, jobject thiz, jlong handle) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    if (engine) engine->play();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_pause(JNIEnv *env, jobject thiz, jlong handle) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    if (engine) engine->pause();
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_init(JNIEnv *env, jobject thiz, jint sample_rate, jint buffer_size) {
    AudioEngine* engine = new AudioEngine(sample_rate, buffer_size);
    if (!engine->isValid()) {
        delete engine;
        return 0L;
    }
    return reinterpret_cast<jlong>(engine);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_destroy(JNIEnv *env, jobject thiz, jlong handle) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    if (engine != nullptr){
        delete engine;
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "JNI: Destroy lunched!");
    }
    __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "JNI: engine is nullptr !");
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_loadWav(JNIEnv *env, jobject thiz, jlong handle, jstring asset_path) {
    // TODO: Zaimplementuj wczytywanie pliku WAV z Assets, używając AAssetManager i np. biblioteki 'libsndfile'
    // Na razie zwracamy -1 jako błąd
    __android_log_print(ANDROID_LOG_ERROR, "AudioEngineJNI", "loadWav is not implemented yet!");
    return -1;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_trigger(JNIEnv *env, jobject thiz, jlong handle,
                                                          jint sample_id, jfloat velocity) {
    // TODO: implement trigger()

}extern "C" JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_setBPM(JNIEnv *env, jobject thiz, jlong handle,
                                                         jfloat bpm) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    engine->setBPM(bpm);
}
extern "C" JNIEXPORT jfloat JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_getBPM(JNIEnv *env, jobject thiz, jlong handle) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    return engine->getBPM();
}
extern "C" JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_updateGrid(JNIEnv *env, jobject thiz, jlong handle, jint sample_id, jint step, jboolean is_set) {
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    if (engine) {
        engine->updateGrid(sample_id, step, is_set);
    }
}