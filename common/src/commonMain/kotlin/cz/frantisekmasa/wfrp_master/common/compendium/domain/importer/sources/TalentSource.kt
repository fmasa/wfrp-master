package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface TalentSource {
    fun importTalents(document: Document): List<Talent>
}
