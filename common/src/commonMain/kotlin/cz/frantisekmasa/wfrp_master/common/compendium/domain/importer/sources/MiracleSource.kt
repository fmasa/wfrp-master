package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface MiracleSource {
    fun importMiracles(document: Document) : List<Miracle>
}