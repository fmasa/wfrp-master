package cz.frantisekmasa.wfrp_master.core

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val CoreModule = module {
    single { FirebaseAuth.getInstance() }
    single {
        JsonMapper().apply {
            registerKotlinModule()
        }
    }

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

    single {
        val functions = Firebase.functions
        val url = BuildConfig.FUNCTIONS_EMULATOR_URL

        @Suppress("ConstantConditionIf")
        if (url != "") {
            val parts = url.split(':')

            functions.useEmulator(parts[0], parts[1].toInt())
        }

        functions
    }
}
