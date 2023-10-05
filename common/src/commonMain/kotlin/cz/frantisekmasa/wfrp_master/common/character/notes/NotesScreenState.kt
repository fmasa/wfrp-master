package cz.frantisekmasa.wfrp_master.common.character.notes

import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType

data class NotesScreenState(
    val characterType: CharacterType,
    val partyAmbitions: Ambitions,
    val characterAmbitions: Ambitions,
    val characterNote: String,
    val characterMotivation: String,
)
