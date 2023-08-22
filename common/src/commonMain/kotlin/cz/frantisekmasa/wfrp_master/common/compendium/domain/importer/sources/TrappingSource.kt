package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface TrappingSource {
    fun importTrappings(document: Document): List<Trapping>
}
