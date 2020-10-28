package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.runtime.staticAmbientOf
import cz.muni.fi.rpg.model.authentication.User

val AmbientUser = staticAmbientOf<User> { error("Logged in user was not provided!") }
