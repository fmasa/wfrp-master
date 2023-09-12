package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.StringResource
import java.util.Locale
import javax.annotation.concurrent.Immutable

@Immutable
interface Translator {

    val locale: Locale

    fun translate(name: StringResource): String

    fun interface Factory {
        fun create(language: Language): Translator
    }
}
