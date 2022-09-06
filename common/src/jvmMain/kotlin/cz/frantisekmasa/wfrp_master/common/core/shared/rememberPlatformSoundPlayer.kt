package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.soywiz.korau.format.WAV
import com.soywiz.korau.sound.AudioData
import com.soywiz.korau.sound.playAndWait
import com.soywiz.korau.sound.toStream

@Composable
actual fun rememberPlatformSoundPlayer(sound: Resources.Sound): SoundPlayer {
    return remember(sound) { KorauSoundPlayer(sound) }
}

class KorauSoundPlayer(
    private val sound: Resources.Sound
) : SoundPlayer {
    private var audioData: AudioData? = null

    override suspend fun play() {
        loadAudio().toStream().playAndWait()
    }

    private suspend fun loadAudio(): AudioData {
        val data = audioData

        if (data != null) {
            return data
        }

        val loadedData = SoundPlayer::class.java.getResourceAsStream("/" + sound.path)
            ?.let { WAV.decode(it.readBytes()) }
            ?: error("Could not decode ${sound.path}")

        audioData = loadedData

        return loadedData
    }
}
