package cz.muni.fi.rpg.di

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.model.cache.CharacterRepositoryIdentityMap
import cz.muni.fi.rpg.model.cache.PartyRepositoryIdentityMap
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.model.domain.encounter.NpcRepository
import cz.muni.fi.rpg.model.domain.encounter.EncounterRepository
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.repositories.*
import cz.muni.fi.rpg.model.firestore.repositories.compendium.FirestoreCompendium
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.viewModels.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import java.util.*
import cz.frantisekmasa.wfrp_master.core.firestore.aggregateMapper
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.Npc
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.compendium.Skill as CompendiumSkill
import cz.muni.fi.rpg.model.domain.compendium.Spell as ComepndiumSpell
import cz.muni.fi.rpg.model.domain.compendium.Talent as CompendiumTalent

val appModule = module {
    fun Scope.skillCompendium() = FirestoreCompendium<CompendiumSkill>(
        COLLECTION_COMPENDIUM_SKILLS,
        get(),
        aggregateMapper(CompendiumSkill::class),
    )

    fun Scope.talentCompendium() = FirestoreCompendium<CompendiumTalent>(
        COLLECTION_COMPENDIUM_TALENTS,
        get(),
        aggregateMapper(CompendiumTalent::class),
    )

    fun Scope.spellCompendium() = FirestoreCompendium<ComepndiumSpell>(
        COLLECTION_COMPENDIUM_SPELLS,
        get(),
        aggregateMapper(ComepndiumSpell::class),
    )

    /**
     * Common database stuff
     */
    single {
        val firestore = Firebase.firestore

        @Suppress("ConstantConditionIf")
        if (BuildConfig.FIRESTORE_EMULATOR_URL != "") {
            firestore.firestoreSettings = firestoreSettings {
                host = BuildConfig.FIRESTORE_EMULATOR_URL
                isSslEnabled = false
            }
        }

        firestore
    }

    single<InvitationProcessor> { FirestoreInvitationProcessor(get(), get()) }
    single { FirebaseAuth.getInstance() }
    single {
        val mapper = JsonMapper()
        mapper.registerKotlinModule()

        mapper
    }

    /**
     * Repositories
     */
    single<InventoryItemRepository> { FirestoreInventoryItemRepository(get(), aggregateMapper(InventoryItem::class)) }
    single<CharacterRepository> {
        CharacterRepositoryIdentityMap(10, FirestoreCharacterRepository(get(), aggregateMapper(Character::class)))
    }
    single<PartyRepository> {
        PartyRepositoryIdentityMap(10, FirestorePartyRepository(get(), aggregateMapper(Party::class)))
    }
    single<SkillRepository> { FirestoreSkillRepository(get(), aggregateMapper(Skill::class)) }
    single<TalentRepository> { FirestoreTalentRepository(get(), aggregateMapper(Talent::class)) }
    single<SpellRepository> { FirestoreSpellRepository(get(), aggregateMapper(Spell::class)) }
    single<CharacterFeatureRepository<Armor>> {
        FirestoreCharacterFeatureRepository(Feature.ARMOR, get(), Armor(), aggregateMapper(Armor::class))
    }
    single<EncounterRepository> { FirestoreEncounterRepository(get(), aggregateMapper(Encounter::class)) }
    single<NpcRepository> { FirestoreNpcRepository(get(), aggregateMapper(Npc::class)) }

    single { AdManager(get()) }

    /**
     * ViewModels
     */
    viewModel { (characterId: CharacterId) -> ArmorViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> CharacterStatsViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> CharacterMiscViewModel(characterId, get(), get()) }
    viewModel { (characterId: CharacterId) -> CharacterViewModel(characterId, get()) }
    viewModel { (partyId: UUID) -> EncountersViewModel(partyId, get()) }
    viewModel { (partyId: UUID) -> PartyViewModel(partyId, get()) }
    viewModel { (encounterId: EncounterId) -> EncounterDetailViewModel(encounterId, get(), get()) }
    viewModel { (characterId: CharacterId) -> InventoryViewModel(characterId, get(), get(), get()) }
    viewModel { (characterId: CharacterId) -> SkillsViewModel(characterId, get(), skillCompendium()) }
    viewModel { (characterId: CharacterId) -> SpellsViewModel(characterId, get(), spellCompendium()) }
    viewModel { (characterId: CharacterId) -> TalentsViewModel(characterId, get(), talentCompendium()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { JoinPartyViewModel(get()) }
    viewModel { PartyListViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { (partyId: UUID) -> CharacterCreationViewModel(partyId, get()) }
    viewModel { (partyId: UUID) ->
        CompendiumViewModel(
            partyId,
            skillCompendium(),
            talentCompendium(),
            spellCompendium(),
        )
    }
}