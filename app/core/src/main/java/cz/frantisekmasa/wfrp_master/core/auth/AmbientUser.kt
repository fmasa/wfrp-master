package cz.frantisekmasa.wfrp_master.core.auth

import androidx.compose.runtime.staticAmbientOf

val AmbientUser = staticAmbientOf<User> { error("Logged in user was not provided!") }
