package cz.frantisekmasa.wfrp_master.common.core.auth

import androidx.compose.runtime.compositionLocalOf
import cz.frantisekmasa.wfrp_master.common.core.auth.User

val LocalUser = compositionLocalOf<User> { error("Logged in user was not provided!") }
