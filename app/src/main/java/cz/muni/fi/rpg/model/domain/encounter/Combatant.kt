package cz.muni.fi.rpg.model.domain.encounter

import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.Stats
import java.util.*

data class Combatant(
    val id: UUID,
    val name: String,
    val note: String,
    var wounds: Wounds,
    val stats: Stats,
    val armor: Armor,
    val enemy: Boolean,
    val traits: List<String>,
    val trappings: List<String>,
    val position: Int
) {
    companion object {
        const val NAME_MAX_LENGTH = 100
        const val NOTE_MAX_LENGTH = 400
    }

    init {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(note.length <= NOTE_MAX_LENGTH)
        require(position >= 0)
    }
}