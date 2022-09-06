@file:JvmName("SoundPlayerJvm")

package cz.frantisekmasa.wfrp_master.common.core.shared

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import cz.frantisekmasa.wfrp_master.common.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun rememberPlatformSoundPlayer(sound: Resources.Sound): SoundPlayer {
    val context = LocalContext.current
    val player = remember(sound) { MediaPlayer.create(context, rawResourceId(sound)) }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    return SoundPlayer {
        withContext(Dispatchers.Main) {
            if (player.isPlaying) {
                player.pause()
                player.seekTo(0)
            }

            player.start()
        }
    }
}

private fun rawResourceId(sound: Resources.Sound): Int {
    val imageName = sound.path.substringAfterLast("/").substringBeforeLast(".")
    val rawResourceClass = R.raw::class.java
    val field = rawResourceClass.getDeclaredField(imageName)

    return field.get(rawResourceClass) as Int
}
