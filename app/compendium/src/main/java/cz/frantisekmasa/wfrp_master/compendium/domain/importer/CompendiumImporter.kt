package cz.frantisekmasa.wfrp_master.compendium.domain.importer

import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent

interface CompendiumImporter {
    suspend fun importSkills(): List<Skill>

    suspend fun importTalents(): List<Talent>

    suspend fun importSpells(): List<Spell>

    suspend fun importBlessings() : List<Blessing>
}