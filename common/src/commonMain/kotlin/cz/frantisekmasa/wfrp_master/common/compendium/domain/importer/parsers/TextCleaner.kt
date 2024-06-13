package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

object TextCleaner {
    fun clean(text: String) =
        text
            .replace("  ", " ")
            .replace("( ", "(")
            .replace(" )", ")")
            .replace(" ,", ",")
}
