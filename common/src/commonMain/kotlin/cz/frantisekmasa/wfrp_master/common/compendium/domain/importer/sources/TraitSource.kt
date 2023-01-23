package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface TraitSource {
    fun importTraits(document: Document): List<Trait>
}
