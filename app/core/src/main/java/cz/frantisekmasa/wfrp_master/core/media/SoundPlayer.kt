package cz.frantisekmasa.wfrp_master.core.media

import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientContext

@Composable
fun rememberSoundPlayer(@RawRes soundResource: Int): SoundPlayer {
    if (!AmbientSoundEnabled.current) {
        return SoundPlayer {}
    }

    val context = AmbientContext.current
    val player = remember { MediaPlayer.create(context, soundResource) }

    onDispose {
        player.release()
    }

    return SoundPlayer {
        if (player.isPlaying) {
            player.pause()
            player.seekTo(0)
        }

        player.start()
    }
}

fun interface SoundPlayer {
    fun play()
}