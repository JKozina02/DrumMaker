import android.R
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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