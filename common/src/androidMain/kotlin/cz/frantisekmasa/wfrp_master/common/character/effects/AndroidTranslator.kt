package cz.frantisekmasa.wfrp_master.common.character.effects

import android.content.Context
import android.content.res.Configuration
import cz.frantisekmasa.wfrp_master.common.localization.Translator
import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.StringResource
import java.util.Locale

class AndroidTranslator(
    context: Context,
    language: Language,
) : Translator {
    private val context =
        context.createConfigurationContext(
            Configuration(context.resources.configuration).apply {
                setLocale(language.locale)
            },
        )

    override val locale: Locale = language.locale

    override fun translate(name: StringResource): String {
        return name.getString(context)
    }
}
