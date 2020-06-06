package cz.muni.fi.rpg.di

import androidx.navigation.fragment.NavHostFragment
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.FirestoreCharacterRepository
import cz.muni.fi.rpg.model.firestore.FirestoreInventoryItemRepository
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import cz.muni.fi.rpg.model.firestore.FirestoreSkillRepository
import cz.muni.fi.rpg.model.firestore.jackson.JacksonAggregateMapper
import cz.muni.fi.rpg.ui.character.CharacterFragment
import cz.muni.fi.rpg.ui.character.CharacterStatsFragment
import cz.muni.fi.rpg.ui.character.InventoryFragment
import cz.muni.fi.rpg.ui.character.edit.CharacterEditFragment
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsFragment
import cz.muni.fi.rpg.ui.character.skills.talents.TalentsFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterInfoFormFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsFormFragment
import cz.muni.fi.rpg.ui.gameMaster.GameMasterFragment
import cz.muni.fi.rpg.ui.partyList.PartyListFragment
import cz.muni.fi.rpg.viewModels.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.dsl.module
import java.util.*
import kotlin.reflect.KClass

private fun <T : Any> aggregateMapper(kclass: KClass<T>) =
    JacksonAggregateMapper(kclass, jacksonTypeRef())

val appModule = module {

    /**
     * Common database stuff
     */
    single {
        val firestore = Firebase.firestore

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
    single<InventoryItemRepository> {
        FirestoreInventoryItemRepository(get(), aggregateMapper(InventoryItem::class))
    }
    single<CharacterRepository> {
        FirestoreCharacterRepository(get(), aggregateMapper(Character::class))
    }
    single<PartyRepository> { FirestorePartyRepository(get(), aggregateMapper(Party::class)) }
    single<SkillRepository> { FirestoreSkillRepository(get(), aggregateMapper(Skill::class)) }
    single<TalentRepository> { FirestoreTalentRepository(get(), aggregateMapper(Talent::class)) }

    /**
     * ViewModels
     */
    viewModel { (partyId: UUID) -> PartyViewModel(get(), partyId) }
    viewModel { (characterId: CharacterId) -> CharacterViewModel(characterId, get())}
    viewModel { (characterId: CharacterId) -> CharacterStatsViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> InventoryViewModel(characterId, get(), get()) }
    viewModel { (characterId: CharacterId) -> SkillsViewModel(characterId, get()) }
    viewModel { (characterId: CharacterId) -> TalentsViewModel(characterId, get()) }
    viewModel { AuthenticationViewModel(get()) }

    /**
     * Fragments
     */
    fragment { CharacterFragment() }
    fragment { GameMasterFragment(get(), get()) }
    fragment { NavHostFragment() }
    fragment { PartyListFragment(get()) }
    fragment { CharacterEditFragment(get()) }
    fragment { CharacterStatsFragment() }
    fragment { CharacterSkillsFragment() }
    fragment { InventoryFragment() }
    fragment { CharacterInfoFormFragment() }
    fragment { CharacterStatsFormFragment() }
    fragment { CharacterCreationFragment(get()) }
    fragment { TalentsFragment() }
}