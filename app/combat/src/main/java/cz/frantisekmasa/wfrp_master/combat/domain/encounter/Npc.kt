package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.core.utils.duplicateName
import cz.frantisekmasa.wfrp_master.inventory.domain.Armor
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Npc(
    @Contextual val id: UUID,
    val name: String,
    val note: String,
    val wounds: Wounds,
    val stats: Stats,
    val armor: Armor,
    val enemy: Boolean,
    val alive: Boolean,
    val traits: List<String>,
    val trappings: List<String>,
    val position: Int,
    val conditions: CurrentConditions = CurrentConditions.none(),
) {
    init {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(note.length <= NOTE_MAX_LENGTH)
        require(position >= 0)
    }

    fun duplicate(position: Int) = copy(
        id = UUID.randomUUID(),
        position = position,
        name = duplicateName(name),
    )

    fun updateCurrentWounds(wounds: Int) = copy(wounds = this.wounds.copy(current = wounds))

    companion object {
        const val NAME_MAX_LENGTH = 100
        const val NOTE_MAX_LENGTH = 400
    }
}
