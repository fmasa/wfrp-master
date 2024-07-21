@file:OptIn(ExperimentalSerializationApi::class)

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
import cz.frantisekmasa.wfrp_master.common.compendium.import.CompendiumBundle
import cz.frantisekmasa.wfrp_master.common.core.domain.ExceptionWithUserMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

class JsonCompendiumImporter(stream: InputStream) : CompendiumImporter {
    override val source: String = "json"

    private val data: Result<CompendiumBundle> =
        runCatching {
            try {
                json.decodeFromStream(stream)
            } catch (e: IllegalArgumentException) {
                throw ExceptionWithUserMessage(e.message ?: "Unknown error", e)
            }
        }

    override suspend fun importSkills(): List<Skill> {
        return data.getOrThrow().skills.map { it.toSkill() }
    }

    override suspend fun importTalents(): List<Talent> {
        return data.getOrThrow().talents.map { it.toTalent() }
    }

    override suspend fun importSpells(): List<Spell> {
        return data.getOrThrow().spells.map { it.toSpell() }
    }

    override suspend fun importBlessings(): List<Blessing> {
        return data.getOrThrow().blessings.map { it.toBlessing() }
    }

    override suspend fun importMiracles(): List<Miracle> {
        return data.getOrThrow().miracles.map { it.toMiracle() }
    }

    override suspend fun importTraits(): List<Trait> {
        return data.getOrThrow().traits.map { it.toTrait() }
    }

    override suspend fun importCareers(): List<Career> {
        return data.getOrThrow().careers.map { it.toCareer() }
    }

    override suspend fun importTrappings(): List<Trapping> {
        return data.getOrThrow().trappings.map { it.toTrapping() }
    }

    override suspend fun importDiseases(): List<Disease> {
        return data.getOrThrow().diseases.map { it.toDisease() }
    }

    override suspend fun importJournalEntries(): List<JournalEntry> {
        return data.getOrThrow().journalEntries.map { it.toJournalEntry() }
    }

    companion object {
        private val json =
            Json {
                ignoreUnknownKeys = true
            }
    }
}
