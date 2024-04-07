@file:JvmName("DependencyInjectionJvm")

package cz.frantisekmasa.wfrp_master.common

import android.content.Context
import cz.frantisekmasa.wfrp_master.common.auth.AndroidAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.CommonAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.character.effects.AndroidTranslator
import cz.frantisekmasa.wfrp_master.common.character.effects.Translator
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual val ktorEngine: HttpClientEngine get() = CIO.create()

actual val platformModule = DI.Module("android") {

    bindSingleton { SettingsStorage(instance()) }
    bindSingleton {
        Firebase.firestore.apply {
            @Suppress("KotlinConstantConditions")
            if (BuildConfig.FIRESTORE_EMULATOR_URL != "") {
                val (host, port) = BuildConfig.FIRESTORE_EMULATOR_URL.split(':')
                useEmulator(host, port.toInt())
            }
        }
    }

    bindSingleton { CommonAuthenticationManager(instance(), supportsEmail = true) }
    bindSingleton { AndroidAuthenticationManager(instance(), instance()) }

    bindSingleton {
        Firebase.functions.apply {
            @Suppress("KotlinConstantConditions")
            if (BuildConfig.FUNCTIONS_EMULATOR_URL != "") {
                val (host, port) = BuildConfig.FUNCTIONS_EMULATOR_URL.split(':')
                useEmulator(host, port.toInt())
            }
        }
    }

    bindSingleton {
        val context: Context = instance()
        Translator.Factory { AndroidTranslator(context, it) }
    }
}
