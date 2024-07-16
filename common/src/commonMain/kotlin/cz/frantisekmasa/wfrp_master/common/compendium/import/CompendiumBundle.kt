package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CompendiumBundle(
    val skills: List<SkillImport> = emptyList(),
    val talents: List<TalentImport> = emptyList(),
    val spells: List<SpellImport> = emptyList(),
    val miracles: List<MiracleImport> = emptyList(),
    val blessings: List<BlessingImport> = emptyList(),
    val traits: List<TraitImport> = emptyList(),
    val careers: List<CareerImport> = emptyList(),
    val trappings: List<TrappingImport> = emptyList(),
    val diseases: List<DiseaseImport> = emptyList(),
    val journalEntries: List<JournalEntryImport> = emptyList(),
)
