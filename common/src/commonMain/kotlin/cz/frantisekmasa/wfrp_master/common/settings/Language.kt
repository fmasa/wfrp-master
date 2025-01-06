package cz.frantisekmasa.wfrp_master.common.settings

import java.util.Locale

enum class Language(
    val localizedName: String,
    val englishName: String,
    val locale: Locale,
) {
    EN("English", "English", Locale.ENGLISH),
    FR("Français", "French", Locale.FRENCH),
    ES("Español", "Spanish", Locale("es")),
    DE("Deutsch", "German", Locale.GERMAN),
    PL("Polski", "Polish", Locale("pl")),
    IT("Italiano", "Italian", Locale.ITALIAN),
    RU("Русский", "Russian", Locale("ru")),
    ;

    companion object {
        fun fromCodeOrNull(code: String): Language? {
            return Language.values().firstOrNull { it.name == code }
        }
    }
}
