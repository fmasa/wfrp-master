package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document

interface SkillSource {
    fun importSkills(document: Document): List<Skill>
}
