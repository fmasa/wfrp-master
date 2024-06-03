package cz.frantisekmasa.wfrp_master.common.character

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CharacteristicsScreenState
import cz.frantisekmasa.wfrp_master.common.character.characteristics.CompendiumCareer
import cz.frantisekmasa.wfrp_master.common.character.combat.CharacterCombatScreenState
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionsScreenState
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.notes.NotesScreenState
import cz.frantisekmasa.wfrp_master.common.character.religion.ReligionScreenState
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillDataItem
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenState
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellDataItem
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellGroup
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenState
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentDataItem
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitDataItem
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingItem
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingSaver
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreenState
import cz.frantisekmasa.wfrp_master.common.combat.domain.EquippedWeapon
import cz.frantisekmasa.wfrp_master.common.combat.domain.WornArmourPiece
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.BlessingRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.MiracleRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.sum
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CharacterDetailScreenModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
    private val skills: SkillRepository,
    private val talents: TalentRepository,
    private val traits: TraitRepository,
    private val blessings: BlessingRepository,
    private val miracles: MiracleRepository,
    private val trappings: InventoryItemRepository,
    private val trappingSaver: TrappingSaver,
    private val careerCompendium: Compendium<Career>,
    private val effectManager: EffectManager,
    private val firestore: FirebaseFirestore,
    private val parties: PartyRepository,
    private val userProvider: UserProvider,
    private val spells: SpellRepository,
) : ScreenModel {
    private val character = characters.getLive(characterId).right()
    private val skillsScreenState =
        combine(
            character,
            skills.findAllForCharacter(characterId),
            talents.findAllForCharacter(characterId),
            traits.findAllForCharacter(characterId),
        ) { character, skills, talents, traits ->
            val characteristics = character.characteristics

            SkillsScreenState(
                characteristics = character.characteristics,
                skills =
                    skills.map {
                        SkillDataItem(
                            id = it.id,
                            name = it.name,
                            advances = it.advances,
                            testNumber = characteristics[it.characteristic] + it.advances,
                        )
                    }.toImmutableList(),
                talents =
                    talents.map {
                        TalentDataItem(
                            id = it.id,
                            name = it.name,
                            taken = it.taken,
                        )
                    }.toImmutableList(),
                traits =
                    traits.map {
                        TraitDataItem(
                            id = it.id,
                            name = it.evaluatedName,
                        )
                    }.toImmutableList(),
            )
        }

    private val religionScreenState =
        combine(
            blessings.findAllForCharacter(characterId),
            miracles.findAllForCharacter(characterId),
        ) { blessings, miracles ->
            ReligionScreenState(
                blessings = blessings.toImmutableList(),
                miracles = miracles.toImmutableList(),
            )
        }

    private val maxEncumbrance: Flow<Encumbrance> =
        character
            .map { it.maxEncumbrance }
            .distinctUntilChanged()

    private val trappingsFlow = trappings.findAllForCharacter(characterId)

    private val inventory: Flow<List<TrappingItem>> =
        trappingsFlow.map { items ->
            val (storedItems, notStoredItems) = items.partition { it.containerId != null }
            val storedItemsByContainer = storedItems.groupBy { it.containerId }

            notStoredItems.map { item ->
                val type = item.trappingType

                if (type is TrappingType.Container) {
                    TrappingItem.Container(
                        item,
                        type,
                        storedItemsByContainer[item.id] ?: emptyList(),
                    )
                } else {
                    TrappingItem.SeparateTrapping(item)
                }
            }
        }

    private val currentEncumbrance: Flow<Encumbrance> =
        trappingsFlow.map { items -> items.map { it.effectiveEncumbrance }.sum() }
            .distinctUntilChanged()

    private val money: Flow<Money> =
        character
            .map { it.money }
            .distinctUntilChanged()

    private val trappingsScreenState: Flow<TrappingsScreenState> =
        combine(
            inventory,
            currentEncumbrance,
            maxEncumbrance,
            money,
        ) { trappings, currentEncumbrance, maxEncumbrance, money ->
            TrappingsScreenState(
                currentEncumbrance = currentEncumbrance,
                maxEncumbrance = maxEncumbrance,
                money = money,
                trappings = trappings.toImmutableList(),
            )
        }

    private val characteristicsScreenState: Flow<CharacteristicsScreenState> =
        character
            .map { it.compendiumCareer }
            .distinctUntilChanged()
            .flatMapLatest { characterCareer ->
                if (characterCareer == null) {
                    return@flatMapLatest flowOf(null)
                }

                careerCompendium.getLive(characterId.partyId, characterCareer.careerId)
                    .map {
                        val career = it.orNull() ?: return@map null
                        val level =
                            career.levels.firstOrNull { it.id == characterCareer.levelId }
                                ?: return@map null

                        CompendiumCareer(career, level)
                    }
            }.map { CharacteristicsScreenState(it) }

    private val party: Flow<Party> = parties.getLive(characterId.partyId).right()

    private val characterPickerState: Flow<CharacterPickerState> =
        combine(
            characters.inParty(characterId.partyId, CharacterType.PLAYER_CHARACTER),
            party.map { it.gameMasterId == null || it.gameMasterId == userProvider.userId },
        ) { playerCharacters, isGameMaster ->
            val userId = userProvider.userId

            CharacterPickerState(
                assignableCharacters =
                    if (isGameMaster) {
                        persistentListOf()
                    } else {
                        playerCharacters
                            .asSequence()
                            .filter { it.userId == null }
                            .toImmutableList()
                    },
                allCharacters =
                    if (isGameMaster) {
                        playerCharacters.toImmutableList()
                    } else {
                        playerCharacters
                            .asSequence()
                            .filter { it.userId == userId }
                            .toImmutableList()
                    },
            )
        }

    private val notesScreenState: Flow<NotesScreenState> =
        combine(
            character,
            party,
        ) { character, party ->
            NotesScreenState(
                characterType = character.type,
                partyAmbitions = party.ambitions,
                characterAmbitions = character.ambitions,
                characterNote = character.note,
                characterMotivation = character.motivation,
            )
        }

    private val spellsScreenState: Flow<SpellsScreenState> =
        spells.findAllForCharacter(characterId)
            .map { spells ->
                SpellsScreenState(
                    spells.groupBy { it.lore }
                        .asSequence()
                        .sortedBy { it.key?.ordinal ?: SpellLore.entries.size }
                        .map { (lore, spellsInLore) ->
                            SpellGroup(
                                lore,
                                spellsInLore
                                    .map {
                                        SpellDataItem(
                                            id = it.id,
                                            name = it.name,
                                            castingNumber = it.effectiveCastingNumber,
                                            isMemorized = it.memorized,
                                        )
                                    }
                                    .toImmutableList(),
                            )
                        }
                        .toImmutableList(),
                )
            }
            .distinctUntilChanged()

    private val equippedWeapons: Flow<ImmutableMap<WeaponEquip, List<EquippedWeapon>>> =
        combine(
            trappingsFlow,
            character.map { it.characteristics.strengthBonus }
                .distinctUntilChanged(),
        ) { trappings, strengthBonus ->
            trappings
                .asSequence()
                .mapNotNull { EquippedWeapon.fromTrappingOrNull(it, strengthBonus) }
                .sortedBy { it.trapping.name }
                .groupBy { it.equip }
                .toImmutableMap()
        }

    private val armourPoints: Flow<Armour> = trappingsFlow.map { items -> Armour.fromItems(items) }

    private val armourPieces: Flow<ImmutableMap<HitLocation, ImmutableList<WornArmourPiece>>> =
        trappingsFlow.map { trappings ->
            val locations = mutableMapOf<HitLocation, MutableList<WornArmourPiece>>()

            trappings
                .asSequence()
                .mapNotNull(WornArmourPiece::fromTrappingOrNull)
                .sortedBy { it.trapping.name }
                .forEach { piece ->
                    piece.armour.locations.forEach { location ->
                        locations.getOrPut(location) { mutableListOf() } += (piece)
                    }
                }

            locations
                .mapValues { it.value.toImmutableList() }
                .toImmutableMap()
        }

    private val combatScreenState: Flow<CharacterCombatScreenState> =
        combine(
            equippedWeapons,
            armourPieces,
            armourPoints,
            character
                .map { it.characteristics.toughnessBonus }
                .distinctUntilChanged(),
        ) { equippedWeapons, armourPieces, armourPoints, toughnessBonus ->
            CharacterCombatScreenState(
                equippedWeapons = equippedWeapons,
                armourPieces = armourPieces,
                armourPoints = armourPoints,
                toughnessBonus = toughnessBonus,
            )
        }

    val state: Flow<CharacterDetailScreenState> =
        combine(
            combine(character, party, ::Pair),
            combine(skillsScreenState, religionScreenState, ::Pair),
            combine(characteristicsScreenState, trappingsScreenState, ::Pair),
            combine(notesScreenState, spellsScreenState, ::Pair),
            combine(characterPickerState, combatScreenState, ::Pair),
        ) { (character, party),
            (skillsScreenState, religionScreenState),
            (characteristicsScreenState, trappingsScreenState),
            (notesScreenState, spellsScreenState),
            (characterPickerState, combatScreenState),
            ->
            CharacterDetailScreenState(
                characterId = characterId,
                character = character,
                partyName = party.name,
                isCombatActive = party.activeCombat != null,
                conditionsScreenState = ConditionsScreenState(character.conditions),
                skillsScreenState = skillsScreenState,
                religionScreenState = religionScreenState,
                characteristicsScreenState = characteristicsScreenState,
                notesScreenState = notesScreenState,
                isGameMaster = party.gameMasterId == null || party.gameMasterId == userProvider.userId,
                characterPickerState = characterPickerState,
                trappingsScreenState = trappingsScreenState,
                spellsScreenState = spellsScreenState,
                combatScreenState = combatScreenState,
            )
        }

    suspend fun updateCharacter(change: (Character) -> Character) {
        val character = characters.get(characterId)
        val updatedCharacter = change(character)

        if (updatedCharacter == character) {
            return
        }

        characters.save(characterId.partyId, updatedCharacter)
    }

    fun updatePoints(points: Points) {
        screenModelScope.launch(Dispatchers.IO) {
            updateCharacter { it.updatePoints(points) }
        }
    }

    fun updateConditions(conditions: CurrentConditions) {
        screenModelScope.launch(Dispatchers.IO) {
            updateCharacter { it.updateConditions(conditions) }
        }
    }

    suspend fun updateNote(note: String) {
        updateCharacter { it.copy(note = note) }
    }

    suspend fun updateMotivation(motivation: String) {
        updateCharacter { it.copy(motivation = motivation) }
    }

    suspend fun updateCharacterAmbitions(ambitions: Ambitions) {
        updateCharacter { it.copy(ambitions = ambitions) }
    }

    suspend fun assignCharacter(
        character: Character,
        userId: UserId,
    ) {
        characters.save(characterId.partyId, character.assignToUser(userId))
    }

    fun removeTrait(traitItem: TraitDataItem) {
        screenModelScope.launch(Dispatchers.IO) {
            firestore.runTransaction {
                val trait = traits.find(this, characterId, traitItem.id) ?: return@runTransaction

                effectManager.removeItem(
                    this,
                    parties.get(this, characterId.partyId),
                    characterId,
                    traits,
                    trait,
                )
            }
        }
    }

    fun removeTalent(talentItem: TalentDataItem) {
        screenModelScope.launch(Dispatchers.IO) {
            firestore.runTransaction {
                val talent = talents.find(this, characterId, talentItem.id) ?: return@runTransaction

                effectManager.removeItem(
                    this,
                    parties.get(this, characterId.partyId),
                    characterId,
                    talents,
                    talent,
                )
            }
        }
    }

    fun removeSkill(skill: SkillDataItem) {
        screenModelScope.launch(Dispatchers.IO) {
            skills.remove(characterId, skill.id)
        }
    }

    fun removeBlessing(blessing: Blessing) {
        screenModelScope.launch(Dispatchers.IO) {
            blessings.remove(characterId, blessing.id)
        }
    }

    fun removeMiracle(miracle: Miracle) {
        screenModelScope.launch(Dispatchers.IO) {
            miracles.remove(characterId, miracle.id)
        }
    }

    fun removeTrapping(trapping: InventoryItem) {
        screenModelScope.launch(Dispatchers.IO) {
            trappings.remove(characterId, trapping.id)
        }
    }

    fun removeSpell(spell: SpellDataItem) {
        screenModelScope.launch(Dispatchers.IO) {
            spells.remove(characterId, spell.id)
        }
    }

    suspend fun updateMoneyBalance(money: Money) {
        updateCharacter { it.updateMoneyBalance(money) }
    }

    fun duplicateTrapping(trapping: InventoryItem) {
        screenModelScope.launch(Dispatchers.IO) {
            trappings.save(characterId, trapping.duplicate())
        }
    }

    suspend fun addToContainer(
        trapping: InventoryItem,
        container: InventoryItem,
    ) {
        Napier.d("Trying to store $trapping in $container")

        if (container.containerId != null || trapping.containerId == container.id) {
            return
        }

        val trappingType = trapping.trappingType
        val updatedTrappings = mutableListOf<InventoryItem>()

        // When storing Container X in a Container Y, all items previously stored
        // in X will be stored in Y
        if (trappingType is TrappingType.Container) {
            updatedTrappings.addAll(
                trappingSaver.takeAllItemsFromContainer(characterId, trapping)
                    .map { it.addToContainer(container.id) },
            )
        }

        updatedTrappings += trapping.addToContainer(container.id)

        firestore.runTransaction {
            updatedTrappings.forEach {
                trappings.save(this, characterId, it)
            }
        }
    }
}
