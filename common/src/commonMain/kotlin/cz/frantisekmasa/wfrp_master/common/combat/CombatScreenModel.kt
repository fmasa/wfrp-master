package cz.frantisekmasa.wfrp_master.common.combat

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.combat.domain.ArmourPart
import cz.frantisekmasa.wfrp_master.common.combat.domain.EquippedWeapon
import cz.frantisekmasa.wfrp_master.common.combat.domain.WornArmourPiece
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.BonusesPlus1d10Strategy
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.InitiativeCharacteristicStrategy
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.InitiativePlus1d10Strategy
import cz.frantisekmasa.wfrp_master.common.combat.domain.initiative.InitiativeTestStrategy
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.GroupAdvantage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.InitiativeStrategy
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.MiracleRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.core.ui.FlowStatBlockData
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.encounters.CombatantItem
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlin.random.Random

class CombatScreenModel(
    private val partyId: PartyId,
    private val random: Random,
    private val parties: PartyRepository,
    private val characters: CharacterRepository,
    private val skills: SkillRepository,
    private val talents: TalentRepository,
    private val spells: SpellRepository,
    private val blessings: BlessingRepository,
    private val miracles: MiracleRepository,
    private val traits: TraitRepository,
    private val trappings: InventoryItemRepository,
) : ScreenModel {
    val party: Flow<Party> = parties.getLive(partyId).right()

    private val combatFlow: Flow<Combat> =
        party
            .mapLatest { it.activeCombat }
            .filterNotNull()

    val turn: Flow<Int> =
        combatFlow
            .mapLatest { it.getTurn() }
            .distinctUntilChanged()

    val round: Flow<Int> =
        combatFlow
            .mapLatest { it.getRound() }
            .distinctUntilChanged()

    val groupAdvantage: Flow<GroupAdvantage> =
        combatFlow
            .mapLatest { it.groupAdvantage }
            .distinctUntilChanged()

    suspend fun loadCharacters(): List<Character> = characters.inParty(partyId, CharacterType.PLAYER_CHARACTER).first()

    suspend fun loadNpcs(): List<Character> = characters.inParty(partyId, CharacterType.NPC).first()

    fun getStatBlockData(characterId: CharacterId): FlowStatBlockData {
        val characterFlow = characters.getLive(characterId).right()
        val trappings = trappings.findAllForCharacter(characterId)

        return FlowStatBlockData(
            note = characterFlow.map { it.note }.distinctUntilChanged(),
            skills =
                skills.findAllForCharacter(characterId).map { skills ->
                    // Only basic skills can have 0 advances,
                    // and these can be easily derived from characteristics
                    skills.filter { it.advances > 0 }
                },
            talents = talents.findAllForCharacter(characterId),
            spells = spells.findAllForCharacter(characterId),
            blessings = blessings.findAllForCharacter(characterId),
            miracles = miracles.findAllForCharacter(characterId),
            traits = traits.findAllForCharacter(characterId),
            weapons =
                trappings
                    .combine(characterFlow) { items, character ->
                        items.mapNotNull {
                            EquippedWeapon.fromTrappingOrNull(it, character.characteristics.strengthBonus)
                        }
                    },
            armour =
                trappings.map { items ->
                    val armourPiecesByPart =
                        items.asSequence()
                            .mapNotNull(WornArmourPiece::fromTrappingOrNull)
                            .flatMap {
                                it.armour.locations.map { location -> location to it }
                            }.groupBy({ it.first }, { it.second })

                    HitLocation.values()
                        .asSequence()
                        .filter { it in armourPiecesByPart }
                        .map { ArmourPart(it, armourPiecesByPart.getValue(it)) }
                        .toList()
                },
        )
    }

    suspend fun startCombat(
        encounterId: Uuid,
        characters: List<Character>,
        npcCharacters: Map<Character, Int>,
    ) {
        val globalEncounterId = EncounterId(partyId, encounterId)
        val combatants =
            characters.map {
                characteristics(it) to
                    Combatant(
                        id = uuid4(),
                        characterId = it.id,
                        initiative = 1,
                    )
            } +
                npcCharacters.flatMap { (character, count) ->
                    val characteristics = characteristics(character)

                    if (count == 1) {
                        listOf(
                            characteristics to
                                Combatant(
                                    id = uuid4(),
                                    name = character.publicName,
                                    characterId = character.id,
                                    initiative = 1,
                                ),
                        )
                    } else {
                        (1..count).map { index ->
                            characteristics to
                                Combatant(
                                    id = uuid4(),
                                    characterId = character.id,
                                    initiative = 1,
                                    wounds = character.wounds,
                                    conditions = character.conditions,
                                    name = "${character.publicName ?: character.name} ($index)",
                                )
                        }
                    }
                }

        parties.update(partyId) {
            it.startCombat(globalEncounterId, rollInitiativeForCombatants(it, combatants))
        }

        Reporter.recordEvent("combat_started", mapOf("partyId" to partyId.toString()))
    }

    private suspend fun characteristics(character: Character): Stats {
        val talents = talents.findAllForCharacter(CharacterId(partyId, character.id)).first()

        val initiativeIncrease =
            talents
                .filter { it.name.equals("Combat Reflexes", ignoreCase = true) }
                .sumOf { it.taken } * 10

        return character.characteristics + Stats.ZERO.copy(initiative = initiativeIncrease)
    }

    suspend fun previousTurn() = updateCombat { it.previousTurn() }

    suspend fun nextTurn() = updateCombat { it.nextTurn() }

    suspend fun reorderCombatants(combatants: List<Combatant>) = updateCombat { it.reorderCombatants(combatants) }

    suspend fun removeCombatant(combatantId: Uuid) {
        updateCombat { it.removeCombatant(combatantId) }
    }

    fun combatants(): Flow<List<CombatantItem>> {
        val charactersFlow =
            characters
                .inParty(partyId, CharacterType.values().toSet())
                .distinctUntilChanged()

        val combatantsFlow =
            party
                .mapNotNull { it.activeCombat?.getCombatants() }
                .distinctUntilChanged()

        return combatantsFlow.combine(charactersFlow) { combatants, characters ->
            val charactersById = characters.associateBy { it.id }

            combatants
                .map { combatant ->
                    val character = charactersById[combatant.characterId] ?: return@map null

                    CombatantItem(
                        characterId = CharacterId(partyId, character.id),
                        character = character,
                        combatant = combatant,
                    )
                }.filterNotNull()
        }
    }

    suspend fun endCombat() = parties.update(partyId) { it.endCombat() }

    private fun rollInitiativeForCombatants(
        party: Party,
        combatants: List<Pair<Stats, Combatant>>,
    ): List<Combatant> {
        val strategy = initiativeStrategy(party)

        return combatants.shuffled(random) // Shuffle combatants first to randomize result for ties
            .map { (characteristics, combatant) -> strategy.determineInitiative(characteristics) to combatant }
            .sortedByDescending { (initiativeOrder, _) -> initiativeOrder }
            .map { (initiativeOrder, combatant) -> combatant.withInitiative(initiativeOrder.toInt()) }
    }

    private fun initiativeStrategy(party: Party) =
        when (party.settings.initiativeStrategy) {
            InitiativeStrategy.INITIATIVE_CHARACTERISTIC -> InitiativeCharacteristicStrategy(random)
            InitiativeStrategy.INITIATIVE_TEST -> InitiativeTestStrategy(random)
            InitiativeStrategy.INITIATIVE_PLUS_1D10 -> InitiativePlus1d10Strategy(random)
            InitiativeStrategy.BONUSES_PLUS_1D10 -> BonusesPlus1d10Strategy(random)
        }

    private suspend fun updateCombat(update: (Combat) -> Combat?) =
        parties.update(partyId) { party ->
            val combat = party.activeCombat

            if (combat == null) {
                Napier.w("Trying to update non-existing combat")
                return@update party
            }

            val updatedCombat = update(combat)

            if (updatedCombat == null) {
                party.endCombat()
            } else {
                party.updateCombat(updatedCombat)
            }
        }

    suspend fun updateWounds(
        combatant: CombatantItem,
        wounds: Wounds,
    ) {
        if (combatant.combatant.wounds != null) {
            // Wounds are combatant specific (there may be multiple combatants of same character)
            updateCombat { it.updateCombatant(combatant.combatant.withWounds(wounds)) }
            return
        }

        val character = characters.get(combatant.characterId)
        val points = character.points

        if (points.wounds == wounds.current) {
            return
        }

        characters.save(partyId, character.updatePoints(points.copy(wounds = wounds.current)))
    }

    suspend fun updateConditions(
        combatant: CombatantItem,
        conditions: CurrentConditions,
    ) {
        if (combatant.combatant.conditions != null) {
            // Conditions are combatant specific (there may be multiple combatants of same character)
            updateCombat { it.updateCombatant(combatant.combatant.withConditions(conditions)) }
            return
        }

        val character = characters.get(combatant.characterId)

        if (character.conditions == conditions) {
            return
        }

        characters.save(partyId, character.updateConditions(conditions))
    }

    suspend fun updateAdvantage(
        combatant: Combatant,
        advantage: Advantage,
    ) {
        if (advantage == combatant.advantage) {
            return
        }

        updateCombat { combat ->
            combat.updateCombatant(combatant.withAdvantage(advantage))
        }
    }

    suspend fun updateGroupAdvantage(groupAdvantage: GroupAdvantage) {
        updateCombat { it.updateGroupAdvantage(groupAdvantage) }
    }
}
