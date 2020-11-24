package cz.muni.fi.rpg.model.domain.compendium.importer

import cz.muni.fi.rpg.model.domain.compendium.Skill
import cz.muni.fi.rpg.model.domain.compendium.Spell
import cz.muni.fi.rpg.model.domain.compendium.Talent

interface CompendiumImporter {
    suspend fun importSkills(): List<Skill>

    suspend fun importTalents(): List<Talent>

    suspend fun importSpells(): List<Spell>
}