package cz.muni.fi.rpg.di

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.FirestoreCharacterRepository
import cz.muni.fi.rpg.model.firestore.FirestoreInventoryItemRepository
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import cz.muni.fi.rpg.model.firestore.FirestoreSkillRepository
import cz.muni.fi.rpg.model.firestore.jackson.JacksonAggregateMapper
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.PartyViewModel
import org.koin.android.viewmodel.dsl.viewModel
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

    single<PartyRepository> {
        FirestorePartyRepository(get(), aggregateMapper(Party::class))
    }

    single<SkillRepository> {
        FirestoreSkillRepository(get(), aggregateMapper(Skill::class))
    }

    /**
     * ViewModels
     */
    viewModel { (partyId: UUID) -> PartyViewModel(get(), partyId) }
    viewModel { (characterId: CharacterId) -> CharacterViewModel(get(), get(), get(), characterId)}
}