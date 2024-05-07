package cz.frantisekmasa.wfrp_master.common

import cz.frantisekmasa.wfrp_master.common.auth.CommonAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.JvmAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.character.effects.JvmTranslator
import cz.frantisekmasa.wfrp_master.common.character.effects.Translator
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal actual val platformModule =
    DI.Module("jvm") {
        bindSingleton {
            HttpClient(CIO) {
                install(HttpCache)
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                        },
                    )
                }
            }
        }

        bindSingleton { SettingsStorage() }

        bindSingleton { CommonAuthenticationManager(instance(), supportsEmail = false) }
        bindSingleton { JvmAuthenticationManager(instance(), instance(), instance()) }

        bindSingleton { Translator.Factory { JvmTranslator(it) } }
    }
