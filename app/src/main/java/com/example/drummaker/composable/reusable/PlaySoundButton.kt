import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay


@Composable
fun PlaySoundButton() {
    val context = LocalContext.current
    val fileNames: MutableList<String> = mutableListOf("hihatclose1", "kick1", "snare1")
    val soundPlayer = SoundPlayer(context)

    val coroutineScope = rememberCoroutineScope()
    val bpm = 240
    val delayMs = 60000L / bpm

    Button(onClick = {
        coroutineScope.launch {
            repeat(4) {
                soundPlayer.PlaySelectedSounds(listOf(0,1))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,2))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,1))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,1))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,2))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0))
                delay(delayMs)
            }
        }
    }) {
        Text("Odtwórz dźwięk")
    }
}
@Composable
fun PlaySoundRepeatedlyButton() {
    val context = LocalContext.current
    val fileNames: MutableList<String> = mutableListOf("hihatclose1", "kick1", "snare1")
    val soundPlayer = SoundPlayer(context)

    val coroutineScope = rememberCoroutineScope()
    val bpm = 240
    val delayMs = 60000L / bpm

    Button(onClick = {
        coroutineScope.launch {
            repeat(4) {
                soundPlayer.PlaySelectedSounds(listOf(0,1))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,2))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,1))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,1))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0,2))
                delay(delayMs)
                soundPlayer.PlaySelectedSounds(listOf(0))
                delay(delayMs)
            }
        }
    }) {
        Text("Odtwórz dźwięk")
    }
}