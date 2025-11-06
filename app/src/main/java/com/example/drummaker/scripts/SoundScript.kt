import android.R
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.remember
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class SoundPlayer(private val context: Context, fileNames: List<String>, tempo: Int, ) {

    private var players: MutableList<ExoPlayer> = mutableListOf()
    private var tempo: Int = 1000;

    init {
        for (fileName in fileNames){
            val player =  ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true)
                .build()
            val uriString = "android.resource://${context.packageName}/raw/$fileName"
            val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
            player.setMediaItem(mediaItem)
            player.prepare()
            this.players.add(player)
        }
        this.tempo = tempo
    }

    fun playAllSound() {
        for (player in this.players)
        {
            player.play()
        }
        for (player in this.players)
        {
            player.seekTo(0)
        }
    }

    fun playSelectedSounds(indices: List<Int>) {
        for (index in indices) {
            if (index in players.indices) {
                players[index].play()
                players[index].seekTo(0)
            }
        }
    }
}