package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface DiseaseSource {
    fun importDiseases(document: Document): List<Disease>
}
