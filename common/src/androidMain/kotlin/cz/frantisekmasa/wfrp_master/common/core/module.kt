package cz.frantisekmasa.wfrp_master.common.core

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.common.BuildConfig
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.koin.dsl.module

val CoreModule = module {
    single { FirebaseAuth.getInstance() }


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

    single {
        Json {
            encodeDefaults = true
            serializersModule = SerializersModule {
                contextual(UuidSerializer())
            }
        }
    }
}
