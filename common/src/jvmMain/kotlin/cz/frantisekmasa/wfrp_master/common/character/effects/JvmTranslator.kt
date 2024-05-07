package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.StringResource
import java.util.Locale

class JvmTranslator(
    private val language: Language,
) : Translator {
    override val locale: Locale = language.locale

    override fun translate(name: StringResource): String {
        return name.localized(language.locale).lowercase(locale)
    }
}
