package cz.frantisekmasa.wfrp_master.combat.ui

import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.NpcRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*

class CombatViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository,
    private val npcs: NpcRepository,
    private val characters: CharacterRepository,
) {

    val party: Flow<Party> = parties.getLive(partyId).right()

    private val combat: Flow<Combat> = party
        .mapLatest { it.getActiveCombat() }
        .filterNotNull()

    val turn: Flow<Int> = combat
        .mapLatest { it.getTurn() }
        .distinctUntilChanged()

    val round: Flow<Int> = combat
        .mapLatest { it.getRound() }
        .distinctUntilChanged()

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

    suspend fun reorderCombatants(combatants: List<Combatant>) =
        updateCombat { it.reorderCombatants(combatants) }

    fun combatants(): Flow<List<CombatantItem>> {
        val npcsFlow = party
            .mapNotNull { it.getActiveCombat()?.encounterId }
            .distinctUntilChanged()
            .transform { emitAll(npcs.findByEncounter(EncounterId(partyId, it))) }

        val charactersFlow = characters
            .inParty(partyId)
            .distinctUntilChanged()

        val combatantsFlow = party
            .mapNotNull { it.getActiveCombat()?.getCombatants() }
            .distinctUntilChanged()

        return combineFlows(
            combatantsFlow,
            npcsFlow,
            charactersFlow
        ) { combatants, npcs, characters ->
            val npcsById = npcs.associateBy { it.id }
            val charactersById = characters.associateBy { it.id }

            combatants
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

    private suspend fun updateCombat(update: (Combat) -> Combat) {
        val party = parties.get(partyId)
        val combat = party.getActiveCombat()

        if (combat == null) {
            Timber.w("Trying to update non-existing combat")
            return
        }

        party.updateCombat(update(combat))

        parties.save(party)
    }

    private fun <T1, T2, T3, R> combineFlows(
        first: Flow<T1>,
        second: Flow<T2>,
        third: Flow<T3>,
        transform: suspend (T1, T2, T3) -> R,
    ): Flow<R> =
        first.combine(second) { a, b -> Pair(a, b) }
            .combine(third) { (a, b), c -> transform(a, b, c) }
}