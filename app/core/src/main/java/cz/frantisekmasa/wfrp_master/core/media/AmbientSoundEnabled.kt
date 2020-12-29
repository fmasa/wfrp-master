package cz.frantisekmasa.wfrp_master.core.media

import androidx.compose.runtime.staticAmbientOf

val AmbientSoundEnabled = staticAmbientOf<Boolean> { error("Logged in user was not provided!") }
