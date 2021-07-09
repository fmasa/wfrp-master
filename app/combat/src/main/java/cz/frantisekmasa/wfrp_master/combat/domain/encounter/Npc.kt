package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.inventory.domain.Armor
import java.util.UUID

/**
 * TODO: Turn into immutable data class
 */
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

    private fun validate(name: String, note: String) {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(note.length <= NOTE_MAX_LENGTH)
    }

    fun updateCurrentWounds(wounds: Int) {
        this.wounds = this.wounds.copy(current = wounds)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Npc

        if (id != other.id) return false
        if (position != other.position) return false
        if (name != other.name) return false
        if (note != other.note) return false
        if (wounds != other.wounds) return false
        if (stats != other.stats) return false
        if (armor != other.armor) return false
        if (enemy != other.enemy) return false
        if (alive != other.alive) return false
        if (traits != other.traits) return false
        if (trappings != other.trappings) return false
        if (conditions != other.conditions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + position
        result = 31 * result + name.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + wounds.hashCode()
        result = 31 * result + stats.hashCode()
        result = 31 * result + armor.hashCode()
        result = 31 * result + enemy.hashCode()
        result = 31 * result + alive.hashCode()
        result = 31 * result + traits.hashCode()
        result = 31 * result + trappings.hashCode()
        result = 31 * result + conditions.hashCode()
        return result
    }
}
