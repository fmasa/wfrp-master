package cz.muni.fi.rpg.di

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.revenuecat.purchases.Purchases
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.EncounterRepository
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.combat.ui.CombatViewModel
import cz.frantisekmasa.wfrp_master.compendium.infrastructure.FirestoreCompendium
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumViewModel
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.model.cache.CharacterRepositoryIdentityMap
import cz.muni.fi.rpg.model.cache.PartyRepositoryIdentityMap
import cz.frantisekmasa.wfrp_master.core.domain.Armor
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.NpcRepository
import cz.frantisekmasa.wfrp_master.combat.infrastructure.FirestoreEncounterRepository
import cz.frantisekmasa.wfrp_master.combat.infrastructure.FirestoreNpcRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Feature
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemRepository
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.repositories.*
import cz.frantisekmasa.wfrp_master.core.ads.AdManager
import cz.frantisekmasa.wfrp_master.core.ads.AdViewModel
import cz.muni.fi.rpg.viewModels.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import cz.frantisekmasa.wfrp_master.core.firestore.aggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterRepository
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestorePartyRepository
import cz.frantisekmasa.wfrp_master.core.viewModel.PartyViewModel
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.ui.InventoryViewModel
import cz.frantisekmasa.wfrp_master.core.ads.AdmobLocationProvider
import cz.frantisekmasa.wfrp_master.core.ads.LocationProvider
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.partySettings.PartySettingsViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.SettingsViewModel
import kotlin.random.Random
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill as CompendiumSkill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell as ComepndiumSpell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent as CompendiumTalent

val appModule = module {
    fun Scope.skillCompendium() = FirestoreCompendium(
        COLLECTION_COMPENDIUM_SKILLS,
        get(),
        aggregateMapper(CompendiumSkill::class),
    )

    fun Scope.talentCompendium() = FirestoreCompendium(
        COLLECTION_COMPENDIUM_TALENTS,
        get(),
        aggregateMapper(CompendiumTalent::class),
    )

    fun Scope.spellCompendium() = FirestoreCompendium(
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

    single<LocationProvider> { AdmobLocationProvider() }
    single {
        Purchases.configure(get(), "TGwuwqSQDUkhhYUtPGLdWilEzpOosKVU").apply {
            Purchases.debugLogsEnabled = BuildConfig.DEBUG
        }
    }
    /**
     * Repositories
     */
    single<InventoryItemRepository> { FirestoreInventoryItemRepository(get(), aggregateMapper(
        InventoryItem::class)) }
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
    viewModel { (partyId: PartyId) -> EncountersViewModel(partyId, get()) }
    viewModel { (partyId: PartyId) -> PartyViewModel(partyId, get()) }
    viewModel { (encounterId: EncounterId) -> EncounterDetailViewModel(encounterId, get(), get(), get()) }
    viewModel { (characterId: CharacterId) -> InventoryViewModel(characterId, get(), get(), get()) }
    viewModel { (characterId: CharacterId) -> SkillsViewModel(characterId, get(), skillCompendium()) }
    viewModel { (characterId: CharacterId) -> SpellsViewModel(characterId, get(), spellCompendium()) }
    viewModel { (characterId: CharacterId) -> TalentsViewModel(characterId, get(), talentCompendium()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { NetworkViewModel(get()) }
    viewModel { JoinPartyViewModel(get(), get(), get()) }
    viewModel { PartyListViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get()) }
    viewModel { PremiumViewModel(get()) }
    viewModel { AdViewModel(get()) }
    viewModel { (partyId: PartyId) -> CharacterCreationViewModel(partyId, get()) }
    viewModel { (partyId: PartyId) ->
        CompendiumViewModel(
            partyId,
            skillCompendium(),
            talentCompendium(),
            spellCompendium(),
            get(),
        )
    }
    viewModel { (partyId: PartyId) -> GameMasterViewModel(partyId, get(), get()) }
    viewModel { (partyId: PartyId) -> SkillTestViewModel(partyId, skillCompendium(), get(), get()) }
    viewModel { (partyId: PartyId) -> CombatViewModel(partyId, Random, get(), get(), get()) }
    viewModel { (partyId: PartyId) -> PartySettingsViewModel(partyId, get()) }
}