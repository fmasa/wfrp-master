package cz.frantisekmasa.wfrp_master.combat.ui

import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.NpcRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.*
import java.util.*

class CombatViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository,
    private val npcs: NpcRepository,
    private val characters: CharacterRepository,
) {

    val party: Flow<Party> by lazy { parties.getLive(partyId).right() }

    suspend fun loadNpcsFromEncounter(encounterId: EncounterId): List<Npc> =
        npcs.findByEncounter(encounterId).first()

    suspend fun loadCharacters(): List<Character> = characters.inParty(partyId).first()

    suspend fun startCombat(
        encounterId: EncounterId,
        characters: List<Character>,
        npcs: List<Npc>
    ) {
        val party = parties.get(partyId)
        val combatants =
            characters.map { Combatant.Character(it.id, 1) } +
                npcs.map { Combatant.Npc(NpcId(encounterId, it.id), 1) }

        party.startCombat(encounterId, rollInitiativeForCombatants(combatants))

        parties.save(party)
    }

    suspend fun hasActiveCombat(): Boolean {
        return parties.get(partyId).hasActiveCombat()
    }

    fun combatants(): Flow<List<CombatantItem>> {
        val partyFlow = parties.getLive(partyId).right()

        val npcsFlow = partyFlow
            .mapNotNull { it.getActiveCombat()?.encounterId }
            .distinctUntilChanged()
            .transform { emitAll(npcs.findByEncounter(EncounterId(partyId, it))) }

        val charactersFlow = characters.inParty(partyId)

        return zip(partyFlow, npcsFlow, charactersFlow) { party, npcs, characters ->
            val combat = party.getActiveCombat() ?: return@zip emptyList()

            val npcsById = npcs.indexBy { it.id }
            val charactersById = characters.indexBy { it.id }

            combat.getCombatants()
                .map { combatant ->
                    when (combatant) {
                        is Combatant.Character -> CombatantItem.Character(
                            charactersById.getValue(combatant.characterId),
                            combatant,
                        )
                        is Combatant.Npc -> CombatantItem.Npc(
                            npcsById.getValue(combatant.npcId.npcId),
                            combatant,
                        )
                    }
                }
        }
    }

    private fun rollInitiativeForCombatants(combatants: List<Combatant>): List<Combatant> {
        return combatants.map { it.withInitiative(1) }
            .sortedBy { it.initiative } // This is very naive approach, but for PoC it's enough
    }

    private fun <T, R> List<T>.indexBy(indexFactory: (T) -> R) =
        this.map { indexFactory(it) to it }
            .toMap()

    private fun <T1, T2, T3, R> zip(
        first: Flow<T1>,
        second: Flow<T2>,
        third: Flow<T3>,
        transform: suspend (T1, T2, T3) -> R,
    ): Flow<R> =
        first
            .zip(second) { a, b -> Pair(a, b) }
            .zip(third) { (a, b), c -> transform(a, b, c) }
}