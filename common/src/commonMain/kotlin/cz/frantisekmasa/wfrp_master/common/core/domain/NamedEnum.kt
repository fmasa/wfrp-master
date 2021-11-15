package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.localization.Strings

interface NamedEnum {
    val nameResolver: (strings: Strings) -> String
}

val NamedEnum.localizedName: String @Composable get() = nameResolver(LocalStrings.current)
val NamedEnum.nameRes: Int get() = error("Use nameResolver or name val instead")