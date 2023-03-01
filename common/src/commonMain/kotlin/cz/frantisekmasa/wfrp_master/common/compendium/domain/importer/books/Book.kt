package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure

sealed interface Book : PdfStructure {
    val name: String
}
