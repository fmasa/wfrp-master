package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface SpellSource {
    fun importSpells(document: Document): List<Spell>
}
