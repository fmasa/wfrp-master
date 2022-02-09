package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.NpcRepository
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Wounds
import cz.frantisekmasa.wfrp_master.combat.domain.initiative.BonusesPlus1d10Strategy
import cz.frantisekmasa.wfrp_master.combat.domain.initiative.InitiativeCharacteristicStrategy
import cz.frantisekmasa.wfrp_master.combat.domain.initiative.InitiativePlus1d10Strategy
import cz.frantisekmasa.wfrp_master.combat.domain.initiative.InitiativeTestStrategy
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform
import kotlin.math.max
import kotlin.random.Random

class CombatViewModel(
    private val partyId: PartyId,
    private val random: Random,
    private val parties: PartyRepository,
    private val npcs: NpcRepository,
    private val characters: CharacterRepository,
) : ViewModel() {

    val party: Flow<Party> = parties.getLive(partyId).right()

    private val combatFlow: Flow<Combat> = party
        .mapLatest { it.activeCombat }
        .filterNotNull()

    private val activeEncounterId: Flow<EncounterId> = combatFlow
        .mapLatest { EncounterId(partyId, it.encounterId) }
        .distinctUntilChanged()

    val isCombatActive: Flow<Boolean> = party
        .mapLatest { it.activeCombat != null }
        .distinctUntilChanged()

    val turn: Flow<Int> = combatFlow
        .mapLatest { it.getTurn() }
        .distinctUntilChanged()

    val round: Flow<Int> = combatFlow
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
        val combatants =
            characters.map { it.characteristics to Combatant.Character(it.id, 1) } +
                npcs.map { it.stats to Combatant.Npc(NpcId(encounterId, it.id), 1) }

        parties.update(partyId) {
            it.startCombat(encounterId, rollInitiativeForCombatants(it, combatants))
        }

        Firebase.analytics.logEvent("combat_started") {
            param("partyId", partyId.toString())
        }
    }

    suspend fun previousTurn() = updateCombat { it.previousTurn() }

    suspend fun nextTurn() = updateCombat { it.nextTurn() }

    suspend fun reorderCombatants(combatants: List<Combatant>) =
        updateCombat { it.reorderCombatants(combatants) }

    fun combatants(): Flow<List<CombatantItem>> {
        val npcsFlow = activeEncounterId.transform { emitAll(npcs.findByEncounter(it)) }

        val charactersFlow = characters
            .inParty(partyId)
            .distinctUntilChanged()

        val combatantsFlow = party
            .mapNotNull { it.activeCombat?.getCombatants() }
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
                        is Combatant.Character -> {
                            val character = charactersById[combatant.characterId] ?: return@map null

                            CombatantItem.Character(
                                characterId = CharacterId(partyId, character.id),
                                character = character,
                                combatant = combatant,
                            )
                        }
                        is Combatant.Npc -> {
                            val npc = npcsById[combatant.npcId.npcId] ?: return@map null

                            CombatantItem.Npc(
                                npcId = combatant.npcId,
                                npc = npc,
                                combatant = combatant,
                            )
                        }
                    }
                }.filterNotNull()
        }
    }

    suspend fun endCombat() = parties.update(partyId) { it.endCombat() }

    private fun rollInitiativeForCombatants(party: Party, combatants: List<Pair<Stats, Combatant>>): List<Combatant> {
        val strategy = initiativeStrategy(party)

        return combatants.shuffled(random) // Shuffle combatants first to randomize result for ties
            .map { (characteristics, combatant) -> strategy.determineInitiative(characteristics) to combatant }
            .sortedByDescending { (initiativeOrder, _) -> initiativeOrder }
            .map { (initiativeOrder, combatant) -> combatant.withInitiative(initiativeOrder.toInt()) }
    }

    private fun initiativeStrategy(party: Party) = when (party.settings.initiativeStrategy) {
        InitiativeStrategy.INITIATIVE_CHARACTERISTIC -> InitiativeCharacteristicStrategy(random)
        InitiativeStrategy.INITIATIVE_TEST -> InitiativeTestStrategy(random)
        InitiativeStrategy.INITIATIVE_PLUS_1D10 -> InitiativePlus1d10Strategy(random)
        InitiativeStrategy.BONUSES_PLUS_1D10 -> BonusesPlus1d10Strategy(random)
    }

    private suspend fun updateCombat(update: (Combat) -> Combat) = parties.update(partyId) { party ->
        val combat = party.activeCombat

        if (combat == null) {
            Napier.w("Trying to update non-existing combat")
            return@update party
        }

        party.updateCombat(update(combat))
    }

    private fun <T1, T2, T3, R> combineFlows(
        first: Flow<T1>,
        second: Flow<T2>,
        third: Flow<T3>,
        transform: suspend (T1, T2, T3) -> R,
    ): Flow<R> =
        first.combine(second) { a, b -> Pair(a, b) }
            .combine(third) { (a, b), c -> transform(a, b, c) }

    suspend fun updateWounds(combatant: CombatantItem, wounds: Wounds) {
        when (combatant) {
            is CombatantItem.Character -> {
                val character = characters.get(combatant.characterId)
                val points = character.points

                if (points.wounds == wounds.current) {
                    return
                }

                characters.save(partyId, character.updatePoints(points.copy(wounds = wounds.current)))
            }
            is CombatantItem.Npc -> {
                val npc = npcs.get(combatant.npcId)

                if (npc.wounds == wounds) {
                    return
                }

                npcs.save(combatant.npcId.encounterId, npc.updateCurrentWounds(wounds.current))
            }
        }
    }

    suspend fun updateAdvantage(combatant: Combatant, advantage: Int) {
        val newAdvantage = max(0, advantage)

        if (newAdvantage == combatant.advantage) {
            return
        }

        updateCombat { combat ->
            combat.updateCombatant(combatant.withAdvantage(newAdvantage))
        }
    }
}
