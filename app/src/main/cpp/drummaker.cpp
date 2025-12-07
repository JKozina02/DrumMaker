#include <iostream>
#include <jni.h>
#include <vector>
#include <android/log.h>
#include "oboe/Oboe.h"
#include <cmath>
#include <android/asset_manager_jni.h>
#include "sndfile.h"

struct Sample {
    std::vector<float> buffer;
    int length;
    float sampleRate;
};

struct Voice {
    bool active = false;
    int sampleId = -1;
    float position = 0.0f;
    float velocity = 1.0f;
};

const int NUM_STEPS = 16;
const int MAX_SAMPLES = 8;
const int POLYPHONY = 16;

class AudioEngine : public oboe::AudioStreamDataCallback {

private:
    AAssetManager* assetManager_;
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
    AudioEngine(AAssetManager* assetManager, int sampleRate, int bufferSize);
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

AudioEngine::AudioEngine(AAssetManager* assetManager, int sampleRate, int bufferSize) {
    assetManager_ = assetManager;
    if (assetManager_ == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Asset Manager is null!");
        isStreamValid = false;
        return;
    }
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

struct AssetDataSource { AAsset* asset; };
static sf_count_t asset_read_callback(void *ptr, sf_count_t count, void *user_data) {
    return AAsset_read(static_cast<AssetDataSource*>(user_data)->asset, ptr, count);
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
                if (readPosition >= 0 && readPosition < samples_[voice.sampleId].length) {
                    outputBuffer[i] += samples_[voice.sampleId].buffer[readPosition] * voice.velocity;
                }
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

int AudioEngine::addSample(const char* path) {
    std::lock_guard<std::mutex> lock(mutex_);

    AAsset* asset = AAssetManager_open(assetManager_, path, AASSET_MODE_STREAMING);
    if (!asset) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Nie udało się otworzyć assetu: %s", path);
        return -1;
    }

    AssetDataSource dataSource = { .asset = asset };
    SF_VIRTUAL_IO virtual_io;
    virtual_io.read = asset_read_callback;

    SF_INFO sfinfo;
    SNDFILE* sndfile = sf_open_virtual(&virtual_io, SFM_READ, &sfinfo, &dataSource);

    if (!sndfile) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Błąd otwarcia pliku WAV przez libsndfile: %s", sf_strerror(nullptr));
        AAsset_close(asset);
        return -1;
    }

    Sample newSample;
    newSample.length = sfinfo.frames;
    newSample.buffer.resize(newSample.length);
    sf_read_float(sndfile, newSample.buffer.data(), newSample.length);

    sf_close(sndfile);
    AAsset_close(asset);

    samples_.push_back(std::move(newSample));
    int newSampleId = samples_.size() - 1;
    __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Załadowano sampla: %s, ID: %d", path, newSampleId);
    return newSampleId;
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
        stream_->requestPause();
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Sequencer Paused!");
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
            return;
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
Java_com_example_drummaker_scripts_AudioEngineJNI_init(JNIEnv *env, jobject thiz, jobject asset_manager, jint sample_rate, jint buffer_size) {
    AAssetManager* nativeAssetManager = AAssetManager_fromJava(env, asset_manager);
    AudioEngine* engine = new AudioEngine(nativeAssetManager, sample_rate, buffer_size);
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
    AudioEngine* engine = reinterpret_cast<AudioEngine*>(handle);
    if (!engine) return -1;
    const char* path = env->GetStringUTFChars(asset_path, nullptr);
    int sampleId = engine->addSample(path);
    env->ReleaseStringUTFChars(asset_path, path);
    return sampleId;
}

extern "C" JNIEXPORT void JNICALL
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