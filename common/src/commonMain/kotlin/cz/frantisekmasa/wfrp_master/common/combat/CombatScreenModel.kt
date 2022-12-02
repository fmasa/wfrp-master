package cz.frantisekmasa.wfrp_master.common.combat

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.BonusesPlus1d10Strategy
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.InitiativeCharacteristicStrategy
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.InitiativePlus1d10Strategy
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.InitiativeTestStrategy
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.MiracleRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.core.ui.StatBlockData
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.encounters.CombatantItem
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Npc
import cz.frantisekmasa.wfrp_master.common.encounters.domain.NpcRepository
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform
import kotlin.random.Random

class CombatScreenModel(
    private val partyId: PartyId,
    private val random: Random,
    private val parties: PartyRepository,
    private val npcs: NpcRepository,
    private val characters: CharacterRepository,
    private val skills: SkillRepository,
    private val talents: TalentRepository,
    private val spells: SpellRepository,
    private val blessings: BlessingRepository,
    private val miracles: MiracleRepository,
    private val traits: TraitRepository,
) : ScreenModel {

    val party: Flow<Party> = parties.getLive(partyId).right()

    private val combatFlow: Flow<Combat> = party
        .mapLatest { it.activeCombat }
        .filterNotNull()

    private val activeEncounterId: Flow<EncounterId> = combatFlow
        .mapLatest { EncounterId(partyId, it.encounterId) }
        .distinctUntilChanged()

    val turn: Flow<Int> = combatFlow
        .mapLatest { it.getTurn() }
        .distinctUntilChanged()

    val round: Flow<Int> = combatFlow
        .mapLatest { it.getRound() }
        .distinctUntilChanged()

    suspend fun loadNpcsFromEncounter(encounterId: Uuid): List<Npc> =
        npcs.findByEncounter(EncounterId(partyId, encounterId)).first()

    suspend fun loadCharacters(): List<Character> =
        characters.inParty(partyId, CharacterType.PLAYER_CHARACTER).first()

    suspend fun loadNpcs(): List<Character> =
        characters.inParty(partyId, CharacterType.NPC).first()

    suspend fun getStatBlockData(combatant: CombatantItem): StatBlockData {
        if (combatant !is CombatantItem.Character) {
            return StatBlockData(
                "",
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
            )
        }

        val characterId = combatant.characterId

        return coroutineScope {
            val skillsDeferred = async { skills.findAllForCharacter(characterId).first() }
            val talentsDeferred = async { talents.findAllForCharacter(characterId).first() }
            val spellsDeferred = async { spells.findAllForCharacter(characterId).first() }
            val blessingsDeferred = async { blessings.findAllForCharacter(characterId).first() }
            val miraclesDeferred = async { miracles.findAllForCharacter(characterId).first() }
            val traitsDeferred = async { traits.findAllForCharacter(characterId).first() }

            StatBlockData(
                combatant.note,
                skillsDeferred.await(),
                talentsDeferred.await(),
                spellsDeferred.await(),
                blessingsDeferred.await(),
                miraclesDeferred.await(),
                traitsDeferred.await(),
            )
        }
    }

    suspend fun startCombat(
        encounterId: Uuid,
        characters: List<Character>,
        npcs: List<Npc>,
        npcCharacters: Map<Character, Int>,
    ) {
        val globalEncounterId = EncounterId(partyId, encounterId)
        val combatants =
            characters.map {
                characteristics(it) to Combatant.Character(
                    id = uuid4(),
                    characterId = it.id,
                    initiative = 1,
                )
            } +
                npcs.map {
                    it.stats to Combatant.Npc(
                        id = uuid4(),
                        npcId = NpcId(globalEncounterId, it.id),
                        initiative = 1,
                    )
                } +
                npcCharacters.flatMap { (character, count) ->
                    val characteristics = characteristics(character)

                    if (count == 1)
                        listOf(
                            characteristics to Combatant.Character(
                                id = uuid4(),
                                name = character.publicName,
                                characterId = character.id,
                                initiative = 1,
                            )
                        )
                    else (1..count).map { index ->
                        characteristics to Combatant.Character(
                            id = uuid4(),
                            characterId = character.id,
                            initiative = 1,
                            wounds = character.wounds,
                            conditions = character.conditions,
                            name = "${character.publicName ?: character.name} ($index)",
                        )
                    }
                }

        parties.update(partyId) {
            it.startCombat(globalEncounterId, rollInitiativeForCombatants(it, combatants))
        }

        Reporter.recordEvent("combat_started", mapOf("partyId" to partyId.toString()))
    }

    private suspend fun characteristics(character: Character): Stats {
        val talents = talents.findAllForCharacter(CharacterId(partyId, character.id)).first()

        val initiativeIncrease = talents
            .filter { it.name.equals("Combat Reflexes", ignoreCase = true) }
            .sumOf { it.taken } * 10

        return character.characteristics + Stats.ZERO.copy(initiative = initiativeIncrease)
    }

    suspend fun previousTurn() = updateCombat { it.previousTurn() }

    suspend fun nextTurn() = updateCombat { it.nextTurn() }

    suspend fun reorderCombatants(combatants: List<Combatant>) =
        updateCombat { it.reorderCombatants(combatants) }

    suspend fun removeCombatant(combatantId: Uuid) {
        updateCombat { it.removeCombatant(combatantId) }
    }

    fun combatants(): Flow<List<CombatantItem>> {
        val npcsFlow = activeEncounterId.transform { emitAll(npcs.findByEncounter(it)) }

        val charactersFlow = characters
            .inParty(partyId, CharacterType.values().toSet())
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

    private suspend fun updateCombat(update: (Combat) -> Combat?) = parties.update(partyId) { party ->
        val combat = party.activeCombat

        if (combat == null) {
            Napier.w("Trying to update non-existing combat")
            return@update party
        }

        val updatedCombat = update(combat)

        if (updatedCombat == null)
            party.endCombat()
        else party.updateCombat(updatedCombat)
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
        if (combatant.combatant.wounds != null) {
            // Wounds are combatant specific (there may be multiple combatants of same character)
            updateCombat { it.updateCombatant(combatant.combatant.withWounds(wounds)) }
        }

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

    suspend fun updateConditions(combatant: CombatantItem, conditions: CurrentConditions) {
        if (combatant.combatant.conditions != null) {
            // Conditions are combatant specific (there may be multiple combatants of same character)
            updateCombat { it.updateCombatant(combatant.combatant.withConditions(conditions)) }
            return
        }

        when (combatant) {
            is CombatantItem.Character -> {
                val character = characters.get(combatant.characterId)

                if (character.conditions == conditions) {
                    return
                }

                characters.save(partyId, character.updateConditions(conditions))
            }
            is CombatantItem.Npc -> {
                // NPC do not have conditions, so this must have been handled as combatant specific
            }
        }
    }

    suspend fun updateAdvantage(combatant: Combatant, advantage: Advantage) {
        if (advantage == combatant.advantage) {
            return
        }

        updateCombat { combat ->
            combat.updateCombatant(combatant.withAdvantage(advantage))
        }
    }
}
