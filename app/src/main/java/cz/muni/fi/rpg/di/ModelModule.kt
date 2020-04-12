package cz.muni.fi.rpg.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import cz.muni.fi.rpg.model.PartyRepository
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import cz.muni.fi.rpg.model.infrastructure.UUIDAdapter
import dagger.Module
import dagger.Provides
import java.util.*

@Module
class ModelModule {
    @Provides
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .create()

    @Provides
    fun firestore() = Firebase.firestore

    @Provides
    fun parties(gson: Gson, firestore: FirebaseFirestore): PartyRepository =
        FirestorePartyRepository(gson, firestore)

    @Provides
    fun auth() = FirebaseAuth.getInstance()
}