package cz.frantisekmasa.wfrp_master.common.character.characteristics

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career

class CharacteristicsScreenState(
    val compendiumCareer: CompendiumCareer?,
)

@Immutable
data class CompendiumCareer(
    val career: Career,
    val level: Career.Level,
)
