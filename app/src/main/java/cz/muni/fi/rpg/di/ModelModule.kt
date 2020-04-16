package cz.muni.fi.rpg.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.firestore.FirestoreCharacterRepository
import cz.muni.fi.rpg.model.firestore.FirestoreInvitationProcessor
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import cz.muni.fi.rpg.model.infrastructure.UUIDAdapter
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

@Module
class ModelModule {
    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .create()

    @Provides
    @Singleton
    fun firestore() = Firebase.firestore

    @Provides
    @Singleton
    fun parties(gson: Gson, firestore: FirebaseFirestore): PartyRepository =
        FirestorePartyRepository(gson, firestore)

    @Provides
    @Singleton
    fun characters(gson: Gson, firestore: FirebaseFirestore): CharacterRepository =
        FirestoreCharacterRepository(gson, firestore)

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