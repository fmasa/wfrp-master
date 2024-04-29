@file:JvmName("DependencyInjectionJvm")

package cz.frantisekmasa.wfrp_master.common

import android.content.Context
import cz.frantisekmasa.wfrp_master.common.auth.AndroidAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.CommonAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.character.effects.AndroidTranslator
import cz.frantisekmasa.wfrp_master.common.character.effects.Translator
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual val platformModule = DI.Module("android") {

    bindSingleton { SettingsStorage(instance()) }
    bindSingleton { CommonAuthenticationManager(instance(), supportsEmail = true) }
    bindSingleton { AndroidAuthenticationManager(instance(), instance()) }

    bindSingleton {
        val context: Context = instance()
        Translator.Factory { AndroidTranslator(context, it) }
    }
}
