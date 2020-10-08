package cz.muni.fi.rpg.di

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
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
import cz.muni.fi.rpg.model.firestore.jackson.JacksonAggregateMapper
import cz.muni.fi.rpg.model.firestore.repositories.*
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.viewModels.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

private inline fun <reified T : Any> aggregateMapper() =
    JacksonAggregateMapper(T::class, jacksonTypeRef())

val appModule = module {

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
    single<InventoryItemRepository> {FirestoreInventoryItemRepository(get(), aggregateMapper()) }
    single<CharacterRepository> {
        CharacterRepositoryIdentityMap(10, FirestoreCharacterRepository(get(), aggregateMapper()))
    }
    single<PartyRepository> {
        PartyRepositoryIdentityMap(10, FirestorePartyRepository(get(), aggregateMapper()))
    }
    single<SkillRepository> { FirestoreSkillRepository(get(), aggregateMapper()) }
    single<TalentRepository> { FirestoreTalentRepository(get(), aggregateMapper()) }
    single<SpellRepository> { FirestoreSpellRepository(get(), aggregateMapper()) }
    single<CharacterFeatureRepository<Armor>> {
        FirestoreCharacterFeatureRepository(Feature.ARMOR, get(), Armor(), aggregateMapper())
    }
    single<EncounterRepository> { FirestoreEncounterRepository(get(), aggregateMapper()) }
    single<NpcRepository> {  FirestoreNpcRepository(get(), aggregateMapper()) }

    single { AdManager(get()) }

    /**
     * ViewModels
     */
    viewModel { (characterId: CharacterId) -> ArmorViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> CharacterStatsViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> CharacterMiscViewModel(characterId, get(), get()) }
    viewModel { (characterId: CharacterId) -> CharacterViewModel(characterId, get())}
    viewModel { (partyId: UUID) -> GameMasterViewModel(partyId, get(), get()) }
    viewModel { (partyId: UUID) -> EncountersViewModel(partyId, get()) }
    viewModel { (partyId: UUID) -> PartyViewModel(partyId, get()) }
    viewModel { (encounterId: EncounterId) -> EncounterDetailViewModel(encounterId, get(), get()) }
    viewModel { (characterId: CharacterId) -> InventoryViewModel(characterId, get(), get(), get()) }
    viewModel { (characterId: CharacterId) -> SkillsViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> SpellsViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> TalentsViewModel(characterId, get()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { JoinPartyViewModel(get()) }
    viewModel { PartyListViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { (partyId: UUID) -> CharacterCreationViewModel(partyId, get())}
    viewModel { (partyId: UUID) -> CompendiumViewModel(partyId)}
}