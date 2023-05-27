package cz.frantisekmasa.wfrp_master.common

import com.google.auth.oauth2.IdToken
import com.google.auth.oauth2.IdTokenCredentials
import com.google.cloud.firestore.FirestoreOptions
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.functions.CloudFunctions
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal actual val ktorEngine: HttpClientEngine get() = CIO.create()

internal actual val platformModule = DI.Module("jvm") {
    bindSingleton {
        HttpClient(CIO) {
            install(HttpCache)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    }
                )
            }
        }
    }

    val firebaseProjectId = "dnd-master-58fca"
    val region = "us-central1"

    bindSingleton { SettingsStorage() }
    bindSingleton { AuthenticationManager(instance(), instance(), instance()) }

    bindSingleton { FirebaseTokenHolder() }
    bindSingleton {
        CloudFunctions(
            instance<FirebaseTokenHolder>().getToken(),
            firebaseProjectId,
            region,
            instance(),
        )
    }
    bindSingleton {
        val tokenHolder: FirebaseTokenHolder = instance()
        val firestore = FirestoreOptions.newBuilder()
            .apply {
                System.getProperty("firestoreEmulatorHost")?.let {
                    setEmulatorHost(it)
                }
            }
            .setProjectId(firebaseProjectId)
            .setCredentialsProvider {
                IdTokenCredentials.newBuilder()
                    .setTargetAudience("what is this?")
                    .setIdTokenProvider { _, _ -> IdToken.create(tokenHolder.getToken()) }
                    .build()
            }.build()
            .service

        Firestore(firestore)
    }
//
//    bindSingleton {
//        val functions = Firebase.functions
//        val url = BuildConfig.FUNCTIONS_EMULATOR_URL
//
//        @Suppress("ConstantConditionIf")
//        if (url != "") {
//            val parts = url.split(':')
//
//            functions.useEmulator(parts[0], parts[1].toInt())
//        }
//
//        functions
//    }
}
