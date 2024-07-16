package cz.frantisekmasa.wfrp_master.common.compendium

import cafe.adriel.voyager.core.model.ScreenModel
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
import cz.frantisekmasa.wfrp_master.common.compendium.import.BlessingImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.CareerImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.CompendiumBundle
import cz.frantisekmasa.wfrp_master.common.compendium.import.DiseaseImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.JournalEntryImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.MiracleImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.SkillImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.SpellImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TalentImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TraitImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TrappingImport
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class CompendiumExportScreenModel(
    private val partyId: PartyId,
    private val careerCompendium: Compendium<Career>,
    private val skillCompendium: Compendium<Skill>,
    private val talentsCompendium: Compendium<Talent>,
    private val spellCompendium: Compendium<Spell>,
    private val blessingCompendium: Compendium<Blessing>,
    private val miracleCompendium: Compendium<Miracle>,
    private val traitCompendium: Compendium<Trait>,
    private val trappingCompendium: Compendium<Trapping>,
    private val diseaseCompendium: Compendium<Disease>,
    private val journalEntryCompendium: Compendium<JournalEntry>,
    parties: PartyRepository,
) : ScreenModel {
    val party: Flow<Party> = parties.getLive(partyId).right()

    suspend fun buildExportJson(): String {
        return coroutineScope {
            val skills =
                async {
                    skillCompendium.liveForParty(partyId).first().map(SkillImport::fromSkill)
                }
            val talents =
                async {
                    talentsCompendium.liveForParty(partyId).first().map(TalentImport::fromTalent)
                }
            val spells =
                async {
                    spellCompendium.liveForParty(partyId).first().map(SpellImport::fromSpell)
                }
            val blessings =
                async {
                    blessingCompendium.liveForParty(partyId).first().map(BlessingImport::fromBlessing)
                }
            val miracles =
                async {
                    miracleCompendium.liveForParty(partyId).first().map(MiracleImport::fromMiracle)
                }
            val traits =
                async {
                    traitCompendium.liveForParty(partyId).first().map(TraitImport::fromTrait)
                }
            val careers =
                async {
                    careerCompendium.liveForParty(partyId).first().map(CareerImport::fromCareer)
                }
            val trappings =
                async {
                    trappingCompendium.liveForParty(partyId).first().map(TrappingImport::fromTrapping)
                }
            val diseases =
                async {
                    diseaseCompendium.liveForParty(partyId).first().map(DiseaseImport::fromDisease)
                }
            val journalEntries =
                async {
                    journalEntryCompendium.liveForParty(partyId).first().map(JournalEntryImport::fromJournalEntry)
                }

            val bundle =
                CompendiumBundle(
                    skills = skills.await(),
                    talents = talents.await(),
                    spells = spells.await(),
                    blessings = blessings.await(),
                    miracles = miracles.await(),
                    traits = traits.await(),
                    careers = careers.await(),
                    trappings = trappings.await(),
                    diseases = diseases.await(),
                    journalEntries = journalEntries.await(),
                )

            json.encodeToString(serializer(), bundle)
        }
    }

    companion object {
        private val json =
            Json {
                encodeDefaults = true
            }
    }
}
