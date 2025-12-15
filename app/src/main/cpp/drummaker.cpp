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
    int length = 0;
};

struct Voice {
    bool active = false;
    int sampleId = -1;
    double position = 0.0;
    float velocity = 1.0f;
};

const int MAX_STEPS = 64;
const int MAX_SAMPLES = 6;
const int POLYPHONY = 16;

class AudioEngine : public oboe::AudioStreamDataCallback {
private:
    AAssetManager* assetManager_;
    std::vector<std::optional<Sample>> samples_;
    std::vector<Voice> voices_;
    std::shared_ptr<oboe::AudioStream> stream_;
    bool isStreamValid = false;
    std::mutex mutex_;
    int sampleRate_ = 0;

    float bpm_ = 120.0f;
    int currentStep_ = 0;
    double samplesPerStep_ = 0.0;
    double nextStepSample_ = 0.0;
    double currentSample_ = 0.0;
    bool isPlaying_ = false;

    int numSteps_ = 16;
    bool grid_[MAX_SAMPLES][MAX_STEPS] = {{false}};

public:
    AudioEngine(AAssetManager* assetManager, int sampleRate, int bufferSize);
    ~AudioEngine();
    int addSample(const char* path);
    void removeSample(int sampleId);
    void play();
    void pause();
    void setBPM(float bpm);
    void updateGrid(int sampleId, int step, bool isSet);
    void trigger(int sampleId, float velocity);
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames);
    bool isValid() const { return isStreamValid; }

    void setPatternLength(int numSteps);
};

struct MemoryDataSource {
    const char* data;
    sf_count_t size;
    sf_count_t offset;
};

static sf_count_t mem_get_filelen(void *user_data) { return ((MemoryDataSource *)user_data)->size; }
static sf_count_t mem_seek(sf_count_t offset, int whence, void *user_data) {
    auto *mem = (MemoryDataSource *)user_data;
    switch (whence) {
        case SEEK_SET: mem->offset = offset; break;
        case SEEK_CUR: mem->offset += offset; break;
        case SEEK_END: mem->offset = mem->size + offset; break;
    }
    return mem->offset;
}
static sf_count_t mem_read(void *ptr, sf_count_t count, void *user_data) {
    auto *mem = (MemoryDataSource *)user_data;
    sf_count_t bytes_to_read = (mem->offset + count > mem->size) ? (mem->size - mem->offset) : count;
    memcpy(ptr, mem->data + mem->offset, bytes_to_read);
    mem->offset += bytes_to_read;
    return bytes_to_read;
}
static sf_count_t mem_tell(void *user_data) { return ((MemoryDataSource *)user_data)->offset; }


AudioEngine::AudioEngine(AAssetManager* assetManager, int sampleRate, int bufferSize) {
    assetManager_ = assetManager;
    samples_.resize(MAX_SAMPLES);
    sampleRate_ = sampleRate;
    voices_.resize(POLYPHONY);
    setBPM(bpm_);

    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setSampleRateConversionQuality(oboe::SampleRateConversionQuality::Medium)
            ->setChannelCount(oboe::ChannelCount::Stereo)
            ->setFormat(oboe::AudioFormat::Float)
            ->setAttributionTag("audioPlayback")
            ->setDataCallback(this);

    oboe::Result result = builder.openStream(stream_);

    if (result == oboe::Result::OK && stream_ != nullptr) {
        isStreamValid = true;
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Stream OK! Channels: %d, Exclusive: %d, PerfMode: %d, Rate: %d, Buffer: %d",
                            stream_->getChannelCount(),
                            stream_->getSharingMode() == oboe::SharingMode::Exclusive,
                            stream_->getPerformanceMode(),
                            stream_->getSampleRate(),
                            stream_->getFramesPerCallback());
    } else {
        isStreamValid = false;
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Stream FAILED: %s", oboe::convertToText(result));
    }
}

AudioEngine::~AudioEngine() {
    if(stream_ && isStreamValid){
        stream_->close();
    }
}

int AudioEngine::addSample(const char* path) {
    std::lock_guard<std::mutex> lock(mutex_);

    int firstFreeSlotId = -1;
    for (int i = 0; i < samples_.size(); i++){
        if(!samples_[i].has_value()){
            firstFreeSlotId = i;
            break;
        }
    }

    if(firstFreeSlotId == -1){
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Brak wolnego miejsca w tablicy sampli!");
        return -1;
    }

    if (!assetManager_) return -1;

    AAsset* asset = AAssetManager_open(assetManager_, path, AASSET_MODE_BUFFER);
    if (!asset) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Nie udało się otworzyć assetu: %s", path);
        return -1;
    }

    const char* assetBuffer = (const char*)AAsset_getBuffer(asset);
    sf_count_t assetSize = AAsset_getLength(asset);

    if (assetBuffer == nullptr) {
        AAsset_close(asset);
        return -1;
    }

    MemoryDataSource memDataSource = {assetBuffer, assetSize, 0};

    SF_VIRTUAL_IO virtual_io;
    virtual_io.get_filelen = mem_get_filelen;
    virtual_io.seek = mem_seek;
    virtual_io.read = mem_read;
    virtual_io.tell = mem_tell;

    SF_INFO sfinfo;
    SNDFILE* sndfile = sf_open_virtual(&virtual_io, SFM_READ, &sfinfo, &memDataSource);


    if (!sndfile) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Błąd otwarcia wirtualnego pliku WAV: %s", sf_strerror(nullptr));
        AAsset_close(asset);
        return -1;
    }

    Sample newSample;
    newSample.length = sfinfo.frames;
    newSample.buffer.resize(newSample.length);
    sf_read_float(sndfile, newSample.buffer.data(), newSample.length);

    sf_close(sndfile);
    AAsset_close(asset);

    samples_[firstFreeSlotId] = std::move(newSample);

    __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Załadowano sampla: %s, ID: %d, Frames: %d", path, firstFreeSlotId, newSample.length);
    return firstFreeSlotId;
}

void AudioEngine::removeSample(int sampleId) {
    if (sampleId < 0 || sampleId >= MAX_SAMPLES){
        return;
    }
    std::lock_guard<std::mutex> lock(mutex_);

    if(samples_[sampleId].has_value()){
        samples_[sampleId].reset();
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Usunięto sampla o ID: %d", sampleId);
    }

    for(int i=0; i < MAX_STEPS; i++){
        grid_[sampleId][i] = false;
    }
}

oboe::DataCallbackResult AudioEngine::onAudioReady(oboe::AudioStream* oboeStream, void* audioData, int32_t numFrames) {
    auto* outputBuffer = static_cast<float*>(audioData);
    int channelCount = stream_->getChannelCount();

    std::fill(outputBuffer, outputBuffer + (numFrames * channelCount), 0.0f);

    std::lock_guard<std::mutex> lock(mutex_);

    for (int i = 0; i < numFrames; ++i) {

        if (isPlaying_ && currentSample_ >= nextStepSample_) {

            currentStep_++;
            if (currentStep_ >= numSteps_) {
                currentStep_ = 0;
            }

            for (int sampleId = 0; sampleId < MAX_SAMPLES; ++sampleId) {
                if (grid_[sampleId][currentStep_]) {
                    trigger(sampleId, 1.0f);
                }
            }
            nextStepSample_ += samplesPerStep_;
        }

        float mixedSample = 0.0f;

        for (Voice& voice : voices_) {
            if (voice.active) {
                int readPosition = static_cast<int>(voice.position);
                if (samples_[voice.sampleId].has_value() && readPosition < samples_[voice.sampleId]->length) {
                    mixedSample += samples_[voice.sampleId]->buffer[readPosition] * voice.velocity;
                }

                voice.position += 1.0;
                if (!samples_[voice.sampleId].has_value() || voice.position >= samples_[voice.sampleId]->length) {
                    voice.active = false;
                }
            }
        }
        for (int j = 0; j < channelCount; ++j) {
            outputBuffer[i * channelCount + j] = mixedSample;
        }

        if (isPlaying_) {
            currentSample_++;
        }
    }

    return oboe::DataCallbackResult::Continue;
}

void AudioEngine::play() {
    std::lock_guard<std::mutex> lock(mutex_);
    if(isValid() && !isPlaying_){
        isPlaying_ = true;
        currentSample_ = 0.0;
        currentStep_ = - 1;
        nextStepSample_ = 0.0;
        stream_->requestStart();
        __android_log_print(ANDROID_LOG_INFO, "AudioEngine", "Sequencer started!");
    }
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
    if (sampleId < 0 || sampleId >= samples_.size() || !samples_[sampleId].has_value()) return;
    for (auto& voice : voices_) {
        if (!voice.active) {
            voice.sampleId = sampleId;
            voice.position = 0.0;
            voice.velocity = velocity;
            voice.active = true;
            return;
        }
    }
}

void AudioEngine::updateGrid(int sampleId, int step, bool isSet) {
    if (sampleId < 0 || sampleId >= MAX_SAMPLES || step < 0 || step >= MAX_STEPS) return;
    std::lock_guard<std::mutex> lock(mutex_);
    grid_[sampleId][step] = isSet;
}

void AudioEngine::setBPM(float bpm) {
    std::lock_guard<std::mutex> lock(mutex_);
    bpm_ = bpm;
    if (sampleRate_ > 0) {
        samplesPerStep_ = (60.0 / bpm_) * sampleRate_ / 4.0;
    }
}

void AudioEngine::setPatternLength(int numSteps) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (numSteps > 0 && numSteps <= MAX_STEPS) {
        numSteps_ = numSteps;
        if(currentStep_ >= numSteps){
            currentStep_ = -1;
        }

    }
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_init(JNIEnv *env, jobject thiz, jobject asset_manager, jint sample_rate, jint buffer_size) {
    AAssetManager *nativeAssetManager = AAssetManager_fromJava(env, asset_manager);
    auto *engine = new AudioEngine(nativeAssetManager, sample_rate, buffer_size);
    if (!engine->isValid()) {
        delete engine;
        return 0L;
    }
    return reinterpret_cast<jlong>(engine);
}

JNIEXPORT jint JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_loadWav(JNIEnv *env, jobject thiz, jlong handle, jstring asset_path) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle)) {
        const char *path = env->GetStringUTFChars(asset_path, nullptr);
        int result = engine->addSample(path);
        env->ReleaseStringUTFChars(asset_path, path);
        return result;
    }
    return -1;
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_removeSample(JNIEnv *env, jobject thiz, jlong handle, jint sample_id) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle)) {
        engine->removeSample(sample_id);
    }
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_play(JNIEnv *env, jobject thiz, jlong handle) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle)) engine->play();
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_pause(JNIEnv *env, jobject thiz, jlong handle) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle)) engine->pause();
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_destroy(JNIEnv *env, jobject thiz, jlong handle) {
    delete reinterpret_cast<AudioEngine *>(handle);
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_setBPM(JNIEnv *env, jobject thiz, jlong handle, jfloat bpm) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle)) engine->setBPM(bpm);
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_updateGrid(JNIEnv *env, jobject thiz, jlong handle, jint sample_id, jint step, jboolean is_set) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle))
        engine->updateGrid(sample_id, step, is_set);
}

JNIEXPORT void JNICALL
Java_com_example_drummaker_scripts_AudioEngineJNI_setPatternLength(JNIEnv *env, jobject thiz, jlong handle, jint num_steps) {
    if (auto *engine = reinterpret_cast<AudioEngine *>(handle)) {
        engine->setPatternLength(num_steps);
    }
}
}