@file:JvmName("DependencyInjectionJvm")

package cz.frantisekmasa.wfrp_master.common

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.functions.CloudFunctions
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual val ktorEngine: HttpClientEngine get() = CIO.create()

actual val platformModule = DI.Module("android") {
    bindSingleton { SettingsStorage(instance()) }
    bindSingleton { Firebase.auth }
    bindSingleton { CloudFunctions(instance()) }
    bindSingleton {
        val firestore = Firebase.firestore

        @Suppress("ConstantConditionIf")
        if (BuildConfig.FIRESTORE_EMULATOR_URL != "") {
            firestore.firestoreSettings = firestoreSettings {
                host = BuildConfig.FIRESTORE_EMULATOR_URL
                isSslEnabled = false
            }
        }

        Firestore(firestore)
    }

    bindSingleton { AuthenticationManager(instance()) }

    bindSingleton {
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
