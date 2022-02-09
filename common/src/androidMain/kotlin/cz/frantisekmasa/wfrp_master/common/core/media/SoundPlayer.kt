package cz.frantisekmasa.wfrp_master.common.core.media

import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberSoundPlayer(@RawRes soundResource: Int): SoundPlayer {
    if (!LocalSoundEnabled.current) {
        return SoundPlayer {}
    }

    val context = LocalContext.current
    val player = remember { MediaPlayer.create(context, soundResource) }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
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
