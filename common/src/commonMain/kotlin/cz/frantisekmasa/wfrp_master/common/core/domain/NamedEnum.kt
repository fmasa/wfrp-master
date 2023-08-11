package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

interface NamedEnum {
    val translatableName: StringResource
}

val NamedEnum.localizedName: String @Composable get() = stringResource(translatableName)
