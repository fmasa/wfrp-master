package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSoundEnabled = staticCompositionLocalOf<Boolean> { error("LocalSoundEnabled was not set") }

@Composable
fun rememberSoundPlayer(sound: Resources.Sound): SoundPlayer {
    if (!LocalSoundEnabled.current) {
        return SoundPlayer {}
    }

    return rememberPlatformSoundPlayer(sound)
}

@Composable
expect fun rememberPlatformSoundPlayer(sound: Resources.Sound): SoundPlayer

fun interface SoundPlayer {
    suspend fun play()
}
