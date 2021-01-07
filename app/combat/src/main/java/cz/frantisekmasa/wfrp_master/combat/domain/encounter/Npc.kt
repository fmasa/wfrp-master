package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import cz.frantisekmasa.wfrp_master.core.domain.Armor
import cz.frantisekmasa.wfrp_master.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import java.util.*

class Npc(
    val id: UUID,
    name: String,
    note: String,
    wounds: Wounds,
    stats: Stats,
    armor: Armor,
    enemy: Boolean,
    alive: Boolean,
    traits: List<String>,
    trappings: List<String>,
    val position: Int
) {

    var name: String = name
        private set
    var note: String = note
        private set
    var wounds: Wounds = wounds
        private set
    var stats: Stats = stats
        private set
    var armor: Armor = armor
        private set
    var enemy: Boolean = enemy
        private set
    var alive: Boolean = alive
        private set
    var traits: List<String> = traits
        private set
    var trappings: List<String> = trappings
        private set
    var conditions: CurrentConditions = CurrentConditions.none()
        private set

    companion object {
        const val NAME_MAX_LENGTH = 100
        const val NOTE_MAX_LENGTH = 400
    }

    init {
        validate(name, note)
        require(position >= 0)
    }

    fun update(
        name: String,
        note: String,
        wounds: Wounds,
        stats: Stats,
        armor: Armor,
        enemy: Boolean,
        alive: Boolean,
        traits: List<String>,
        trappings: List<String>
    ) {
        validate(name, note)

        this.name = name
        this.note = note
        this.wounds = wounds
        this.stats = stats
        this.armor = armor
        this.enemy = enemy
        this.alive = alive
        this.traits = traits
        this.trappings = trappings
    }

    override fun equals(other: Any?) = other is Npc && other.id == id
    override fun hashCode() = id.hashCode()

    private fun validate(name: String, note: String) {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(note.length <= NOTE_MAX_LENGTH)
    }

    fun updateCurrentWounds(wounds: Int) {
        this.wounds = this.wounds.copy(current = wounds)
    }
}