package cz.frantisekmasa.wfrp_master.common.localization

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.StringResource
import java.util.Locale

@Immutable
interface Translator {
    val locale: Locale

    fun translate(name: StringResource): String

    fun interface Factory {
        fun create(language: Language): Translator
    }
}
