package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class ItemQuality(
    override val translatableName: StringResource,
) : NamedEnum {
    DURABLE(Str.trappings_qualities_durable),
    FINE(Str.trappings_qualities_fine),
    LIGHTWEIGHT(Str.trappings_qualities_lightweight),
    PRACTICAL(Str.trappings_qualities_practical),
}