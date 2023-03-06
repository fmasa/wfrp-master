package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.Book
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.BlessingSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.MiracleSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SkillSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TraitSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PdfCompendiumImporter(
    private val document: Document,
    private val book: Book,
) : CompendiumImporter {

    private val mutex = Mutex()

    override suspend fun importSkills(): List<Skill> = mutex.withLock {
        if (book !is SkillSource) {
            return emptyList()
        }

        return book.importSkills(document)
    }

    override suspend fun importTalents(): List<Talent> = mutex.withLock {
        if (book !is TalentSource) {
            return emptyList()
        }

        return book.importTalents(document)
    }

    override suspend fun importSpells(): List<Spell> = mutex.withLock {
        if (book !is SpellSource) {
            return emptyList()
        }

        return book.importSpells(document)
    }

    override suspend fun importBlessings(): List<Blessing> = mutex.withLock {
        if (book !is BlessingSource) {
            return emptyList()
        }

        return book.importBlessings(document)
    }

    override suspend fun importMiracles(): List<Miracle> = mutex.withLock {
        if (book !is MiracleSource) {
            return emptyList()
        }

        return book.importMiracles(document)
    }

    override suspend fun importTraits(): List<Trait> = mutex.withLock {
        if (book !is TraitSource) {
            return emptyList()
        }

        return book.importTraits(document)
    }

    override suspend fun importCareers(): List<Career> = mutex.withLock {
        if (book !is CareerSource) {
            return emptyList()
        }

        return book.importCareers(document)
    }
}
