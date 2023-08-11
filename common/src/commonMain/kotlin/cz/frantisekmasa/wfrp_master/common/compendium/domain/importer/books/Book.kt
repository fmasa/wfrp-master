package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure

@Stable
sealed interface Book : PdfStructure {
    val name: String
}
