package cz.frantisekmasa.wfrp_master.compendium.domain.importer

import cz.frantisekmasa.wfrp_master.compendium.domain.*

interface CompendiumImporter {
    suspend fun importSkills(): List<Skill>

    suspend fun importTalents(): List<Talent>

    suspend fun importSpells(): List<Spell>

    suspend fun importBlessings() : List<Blessing>

    suspend fun importMiracles() : List<Miracle>
}