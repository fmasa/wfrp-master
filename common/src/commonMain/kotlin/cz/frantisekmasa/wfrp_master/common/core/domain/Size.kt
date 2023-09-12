package cz.frantisekmasa.wfrp_master.common.core.domain

import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.resources.StringResource

enum class Size(override val translatableName: StringResource) : NamedEnum {
    TINY(Str.character_size_tiny),
    LITTLE(Str.character_size_little),
    SMALL(Str.character_size_small),
    AVERAGE(Str.character_size_average),
    LARGE(Str.character_size_large),
    ENORMOUS(Str.character_size_enormous),
    MONSTROUS(Str.character_size_monstrous),
}
