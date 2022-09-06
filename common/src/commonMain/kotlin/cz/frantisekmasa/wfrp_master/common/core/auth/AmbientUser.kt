package cz.frantisekmasa.wfrp_master.common.core.auth

import androidx.compose.runtime.compositionLocalOf

val LocalUser = compositionLocalOf<User> { error("Logged in user was not provided!") }
