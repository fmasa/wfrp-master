package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface JournalEntrySource {
    fun importJournalEntries(document: Document): List<JournalEntry>
}
