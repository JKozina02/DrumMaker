import android.R
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SoundPlayer(private val context: Context, fileNames: List<String>) {

    private var playersList: MutableList<ExoPlayer> = mutableListOf()
    private var delayMs = 60000L / 60

    init {
        for (fileName in fileNames){
            val player =  ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true)
                .build()
            val uriString = "android.resource://${context.packageName}/raw/$fileName"
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            player.setMediaItem(mediaItem)
            player.prepare()
            this.playersList.add(player)
        }
    }


    fun delaySetted(newBpm: Int){
        this.delayMs = 60000L / newBpm
    }
    fun delayGetter(): Long{
        return this.delayMs
    }

    fun playSelectedSounds(indices: List<Int>) {
        CoroutineScope(Dispatchers.Default).launch {
            for (index in indices) {
                if (index in playersList.indices) {
                    playersList[index].play()
                    playersList[index].seekTo(0)
                }
            }
        }
    }

    fun playRhythm(rhythmList: List<List<Int>>){
        CoroutineScope(Dispatchers.Default).launch {
            for (rhythm in rhythmList) {
                playSelectedSounds(rhythm)
                delay(delayMs)
            }
        }
    }

}

class SoundManager(private val context: Context) {
    private val allSounds = HashMap<String, String>()
    private val selectedSounds = mutableListOf<String?>()

    init {
        for (sound in soundList) {
            val uriString = "android.resource://${context.packageName}/raw/$sound"
            allSounds[sound] = uriString
        }
    }

    fun selectSound(sound: String) {
        selectedSounds.add(sound)
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

    fun addBlank() {
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