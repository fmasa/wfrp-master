package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping

interface CompendiumImporter {
    /**
     * Source name used for analytics
     */
    val source: String

    suspend fun importSkills(): List<Skill>

    suspend fun importTalents(): List<Talent>

    suspend fun importSpells(): List<Spell>

    suspend fun importBlessings(): List<Blessing>

    suspend fun importMiracles(): List<Miracle>

    suspend fun importTraits(): List<Trait>

    suspend fun importCareers(): List<Career>

    suspend fun importTrappings(): List<Trapping>

    suspend fun importDiseases(): List<Disease>

    suspend fun importJournalEntries(): List<JournalEntry>
}
