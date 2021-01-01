package cz.frantisekmasa.wfrp_master.core.di

import androidx.compose.runtime.staticAmbientOf
import org.koin.core.scope.Scope

val AmbientKoinScope = staticAmbientOf<Scope> { error("Koin scope was not defined") }