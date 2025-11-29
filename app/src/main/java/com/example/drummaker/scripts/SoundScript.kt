import android.R
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SoundPlayer(private val context: Context) {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var playersList: MutableList<ExoPlayer> = mutableListOf()
    private var bpm: Int = 120
    private var delayMs = 60000L / bpm
    private var isPlaying = false;

    fun delaySetter(newBpm: Int){
        this.delayMs = 60000L / newBpm
    }
    fun delayAdd(){
        bpm += 1
        delaySetter(bpm)
    }
    fun delaySub(){
        bpm -= 1
        delaySetter(bpm)
    }
    fun bPMGetter(): Int{
        return this.bpm
    }
    fun prepareAllSounds(soundPlayer: SoundManager){
        this.playersList = mutableListOf()
        val soundList = soundPlayer.getAllSound()
        for (fileName in soundList.values){
            val player =  ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true)
                .build()
            val mediaItem = MediaItem.fromUri(Uri.parse(fileName))
            player.setMediaItem(mediaItem)
            player.prepare()
            this.playersList.add(player)
        }
    }

    fun playSoundsLoop() {
        scope.launch {
            if (!isPlaying) return@launch
            for (player in playersList) {
                player.seekTo(0)
                player.play()
            }
            delay(delayMs)
        }
    }
    fun stopPlaying(){
        isPlaying = false
        scope.cancel()
        for(player in playersList) {
            player.seekTo(0)
        }
    }
}


class SoundManager(private val context: Context) {
    private val allSounds = HashMap<String, String>()
    val selectedSounds = mutableListOf<String?>()

    init {
        for (sound in soundList) {
            val uriString = "android.resource://${context.packageName}/raw/$sound"
            allSounds[sound] = uriString
        }
    }
    fun getAllSound(): HashMap<String, String> {
        return allSounds
    }
    fun addSound(sound: String) {
        if(allSounds.containsKey(sound)) selectedSounds.add(sound)
    }
    fun swapSounds(index1: Int, index2: Int) {
        if (index1 in selectedSounds.indices && index2 in selectedSounds.indices) {
            val temp = selectedSounds[index1]
            selectedSounds[index1] = selectedSounds[index2]
            selectedSounds[index2] = temp
        }
    }

    fun removeSound(index: Int) {
        if (index in selectedSounds.indices) {
            selectedSounds.removeAt(index)
        }
    }

    fun addEmpty() {
        selectedSounds.add(null)
    }

    fun removeEmpty() {
        selectedSounds.removeAll { it.isNullOrEmpty() }
    }
}

val soundList: List<String> = listOf(
    "hihatclose1",
    "kick1",
    "snare1"
)