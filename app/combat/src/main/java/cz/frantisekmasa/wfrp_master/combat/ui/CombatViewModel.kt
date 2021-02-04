package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.math.max
import kotlin.random.Random

class CombatViewModel(
    private val partyId: PartyId,
    private val random: Random,
    private val parties: PartyRepository,
    private val npcs: NpcRepository,
    private val characters: CharacterRepository,
): ViewModel() {

    private val partyFlow = parties.getLive(partyId).right()

    val party: LiveData<Party> = partyFlow.asLiveData()

    private val combatFlow: Flow<Combat> = partyFlow
        .mapLatest { it.getActiveCombat() }
        .filterNotNull()

    private val activeEncounterId: Flow<EncounterId> = combatFlow
        .mapLatest { EncounterId(partyId, it.encounterId) }
        .distinctUntilChanged()

    val isCombatActive: LiveData<Boolean> = partyFlow
        .mapLatest { it.hasActiveCombat() }
        .distinctUntilChanged()
        .asLiveData()

    val turn: LiveData<Int> = combatFlow
        .mapLatest { it.getTurn() }
        .distinctUntilChanged()
        .asLiveData()

    val round: LiveData<Int> = combatFlow
        .mapLatest { it.getRound() }
        .distinctUntilChanged()
        .asLiveData()

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
            characters.map { it.getCharacteristics() to Combatant.Character(it.id, 1) } +
                    npcs.map { it.stats to Combatant.Npc(NpcId(encounterId, it.id), 1) }

        party.startCombat(encounterId, rollInitiativeForCombatants(party, combatants))

        parties.save(party)

        Firebase.analytics.logEvent("combat_started") {
            param("partyId", partyId.toString())
        }
    }

    suspend fun previousTurn() = updateCombat { it.previousTurn() }

    suspend fun nextTurn() = updateCombat { it.nextTurn() }

    suspend fun reorderCombatants(combatants: List<Combatant>) =
        updateCombat { it.reorderCombatants(combatants) }

    fun combatants(): LiveData<List<CombatantItem>> {
        val npcsFlow = activeEncounterId.transform { emitAll(npcs.findByEncounter(it)) }

        val charactersFlow = characters
            .inParty(partyId)
            .distinctUntilChanged()

        val combatantsFlow = partyFlow
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
        }.asLiveData()
    }

    suspend fun endCombat() = updateParty { it.endCombat() }

    private fun rollInitiativeForCombatants(party: Party, combatants: List<Pair<Stats, Combatant>>): List<Combatant> {
        val strategy = initiativeStrategy(party)

        return combatants.shuffled(random) // Shuffle combatants first to randomize result for ties
            .map { (characteristics, combatant) -> strategy.determineInitiative(characteristics) to combatant }
            .sortedByDescending { (initiativeOrder, _) -> initiativeOrder }
            .map { (initiativeOrder, combatant) -> combatant.withInitiative(initiativeOrder.toInt()) }
    }

    private fun initiativeStrategy(party: Party) = when(party.getSettings().initiativeStrategy) {
        InitiativeStrategy.INITIATIVE_CHARACTERISTIC -> InitiativeCharacteristicStrategy(random)
        InitiativeStrategy.INITIATIVE_TEST -> InitiativeTestStrategy(random)
        InitiativeStrategy.INITIATIVE_PLUS_1D10 -> InitiativePlus1d10Strategy(random)
        InitiativeStrategy.BONUSES_PLUS_1D10 -> BonusesPlus1d10Strategy(random)
    }

    private suspend fun updateParty(update: (Party) -> Unit) {
        val party = parties.get(partyId)
        val combat = party.getActiveCombat()

        if (combat == null) {
            Timber.w("Trying to update non-existing combat")
            return
        }

        update(party)

        parties.save(party)
    }

    private suspend fun updateCombat(update: (Combat) -> Combat) = updateParty { party ->
        val combat = party.getActiveCombat()

        if (combat == null) {
            Timber.w("Trying to update non-existing combat")
            return@updateParty
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
        when(combatant) {
            is CombatantItem.Character -> {
                val character = characters.get(combatant.characterId)
                val points = character.getPoints()

                if (points.wounds == wounds.current) {
                    return
                }

                character.updatePoints(points.copy(wounds = wounds.current))

                characters.save(partyId, character)
            }
            is CombatantItem.Npc -> {
                val npc = npcs.get(combatant.npcId)

                if (npc.wounds == wounds) {
                    return
                }

                npc.updateCurrentWounds(wounds.current)

                npcs.save(combatant.npcId.encounterId, npc)
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