package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface BlessingSource {
    fun importBlessings(document: Document): List<Blessing>
}
