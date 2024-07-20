package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure

object NullDescriptionParser : TrappingDescriptionParser {
    override fun parse(
        document: Document,
        structure: PdfStructure,
        pages: IntRange,
    ): List<Pair<String, String>> {
        return emptyList()
    }
}
