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
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.firestore.FirestoreCharacterRepository
import cz.muni.fi.rpg.model.firestore.FirestoreInventoryItemRepository
import cz.muni.fi.rpg.model.firestore.FirestoreInvitationProcessor
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import cz.muni.fi.rpg.model.firestore.FirestoreSkillRepository
import cz.muni.fi.rpg.model.firestore.jackson.JacksonAggregateMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModelModule {
    @Provides
    @Singleton
    fun jsonMapper(): JsonMapper {
        val mapper = JsonMapper()
        mapper.registerKotlinModule()

        return mapper
    }

    @Provides
    @Singleton
    fun firestore(): FirebaseFirestore {
        val firestore = Firebase.firestore

        if (BuildConfig.FIRESTORE_EMULATOR_URL != "") {
            firestore.firestoreSettings = firestoreSettings {
                host = BuildConfig.FIRESTORE_EMULATOR_URL
                isSslEnabled = false
            }
        }

        return firestore
    }

    @Provides
    @Singleton
    fun parties(firestore: FirebaseFirestore): PartyRepository =
        FirestorePartyRepository(firestore,
            JacksonAggregateMapper(
                Party::class,
                jacksonTypeRef()
            )
        )

    @Provides
    @Singleton
    fun characters(firestore: FirebaseFirestore): CharacterRepository =
        FirestoreCharacterRepository(
            firestore,
            JacksonAggregateMapper(
                Character::class,
                jacksonTypeRef()
            )
        )

    @Provides
    @Singleton
    fun inventoryItems(firestore: FirebaseFirestore): InventoryItemRepository =
        FirestoreInventoryItemRepository(
            firestore,
            JacksonAggregateMapper(
                InventoryItem::class,
                jacksonTypeRef()
            )
        )

    @Provides
    @Singleton
    fun skills(firestore: FirebaseFirestore): SkillRepository =
        FirestoreSkillRepository(firestore, JacksonAggregateMapper(Skill::class, jacksonTypeRef()))

    @Provides
    @Singleton
    fun invitationProcessor(
        firestore: FirebaseFirestore,
        parties: PartyRepository
    ): InvitationProcessor =
        FirestoreInvitationProcessor(firestore, parties)

    @Provides
    @Singleton
    fun auth() = FirebaseAuth.getInstance()
}