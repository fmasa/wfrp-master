package cz.frantisekmasa.wfrp_master.core.auth

import androidx.compose.runtime.ambientOf

val AmbientUser = ambientOf<User> { error("Logged in user was not provided!") }
