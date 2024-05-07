package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class Availability(
    override val translatableName: StringResource,
) : NamedEnum {
    COMMON(Str.trappings_availability_common),
    SCARCE(Str.trappings_availability_scarce),
    RARE(Str.trappings_availability_rare),
    EXOTIC(Str.trappings_availability_exotic),
}
