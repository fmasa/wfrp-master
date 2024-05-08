package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure

interface TrappingDescriptionParser {
    fun parse(
        document: Document,
        structure: PdfStructure,
        pages: IntRange,
    ): List<Pair<String, String>>

    fun comparableName(name: String): String {
        return name
            .filter { it != '\'' }
            .lowercase()
            .trim()
    }
}
