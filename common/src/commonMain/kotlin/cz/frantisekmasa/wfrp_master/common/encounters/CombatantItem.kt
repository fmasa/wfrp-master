package cz.frantisekmasa.wfrp_master.common.encounters

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Immutable
@Parcelize
data class CombatantItem(
    val characterId: CharacterId,
    private val character: Character,
    val combatant: Combatant,
) : Parcelable {
    fun areSameEntity(other: CombatantItem): Boolean = combatant.areSameEntity(other.combatant)

    val userId: UserId?
        get() = character.userId

    val avatarUrl: String?
        get() = character.avatarUrl

    val name get() = combatant.name ?: character.name

    val characteristics get() = character.characteristics

    val wounds get() = combatant.wounds ?: character.wounds

    val conditions: CurrentConditions get() = combatant.conditions ?: character.conditions

    val note: String get() = character.note
}
