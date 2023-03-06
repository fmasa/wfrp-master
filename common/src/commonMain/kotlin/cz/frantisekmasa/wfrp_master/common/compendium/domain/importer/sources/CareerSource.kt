package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface CareerSource {
    fun importCareers(document: Document): List<Career>
}
