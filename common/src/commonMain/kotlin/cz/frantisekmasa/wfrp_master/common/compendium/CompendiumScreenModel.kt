package cz.frantisekmasa.wfrp_master.common.compendium

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.compendium.import.BlessingImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.CareerImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.CompendiumBundle
import cz.frantisekmasa.wfrp_master.common.compendium.import.MiracleImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.SkillImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.SpellImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TalentImport
import cz.frantisekmasa.wfrp_master.common.compendium.import.TraitImport
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

class CompendiumScreenModel(
    private val partyId: PartyId,
    private val careerCompendium: Compendium<Career>,
    private val skillCompendium: Compendium<Skill>,
    private val talentsCompendium: Compendium<Talent>,
    private val spellCompendium: Compendium<Spell>,
    private val blessingCompendium: Compendium<Blessing>,
    private val miracleCompendium: Compendium<Miracle>,
    private val traitCompendium: Compendium<Trait>,
    parties: PartyRepository,
) : ScreenModel {

    val party: Flow<Party> = parties.getLive(partyId).right()
    val careers: Flow<List<Career>> = careerCompendium.liveForParty(partyId)
    val skills: Flow<List<Skill>> = skillCompendium.liveForParty(partyId)
    val talents: Flow<List<Talent>> = talentsCompendium.liveForParty(partyId)
    val spells: Flow<List<Spell>> = spellCompendium.liveForParty(partyId)
    val blessings: Flow<List<Blessing>> = blessingCompendium.liveForParty(partyId)
    val miracles: Flow<List<Miracle>> = miracleCompendium.liveForParty(partyId)
    val traits: Flow<List<Trait>> = traitCompendium.liveForParty(partyId)

    fun getBlessing(blessingId: Uuid): Flow<Either<CompendiumItemNotFound, Blessing>> {
        return blessingCompendium.getLive(partyId, blessingId)
    }

    fun getSkill(skillId: Uuid): Flow<Either<CompendiumItemNotFound, Skill>> {
        return skillCompendium.getLive(partyId, skillId)
    }

    fun getMiracle(miracleId: Uuid): Flow<Either<CompendiumItemNotFound, Miracle>> {
        return miracleCompendium.getLive(partyId, miracleId)
    }

    fun getSpell(spellId: Uuid): Flow<Either<CompendiumItemNotFound, Spell>> {
        return spellCompendium.getLive(partyId, spellId)
    }

    fun getTalent(talentId: Uuid): Flow<Either<CompendiumItemNotFound, Talent>> {
        return talentsCompendium.getLive(partyId, talentId)
    }

    fun getTrait(traitId: Uuid): Flow<Either<CompendiumItemNotFound, Trait>> {
        return traitCompendium.getLive(partyId, traitId)
    }

    suspend fun save(skill: Skill) {
        skillCompendium.saveItems(partyId, skill)
    }

    suspend fun saveMultipleSkills(skills: List<Skill>) {
        skillCompendium.saveItems(partyId, *skills.toTypedArray())
    }

    suspend fun remove(skill: Skill) {
        skillCompendium.remove(partyId, skill)
    }

    suspend fun save(talent: Talent) {
        talentsCompendium.saveItems(partyId, talent)
    }

    suspend fun remove(talent: Talent) {
        talentsCompendium.remove(partyId, talent)
    }

    suspend fun save(miracle: Miracle) {
        miracleCompendium.saveItems(partyId, miracle)
    }

    suspend fun remove(miracle: Miracle) {
        miracleCompendium.remove(partyId, miracle)
    }

    suspend fun saveMultipleTalents(talents: List<Talent>) {
        talentsCompendium.saveItems(partyId, *talents.toTypedArray())
    }

    suspend fun save(spell: Spell) {
        spellCompendium.saveItems(partyId, spell)
    }

    suspend fun remove(spell: Spell) {
        spellCompendium.remove(partyId, spell)
    }

    suspend fun saveMultipleSpells(spells: List<Spell>) {
        spellCompendium.saveItems(partyId, *spells.toTypedArray())
    }

    suspend fun save(blessing: Blessing) {
        blessingCompendium.saveItems(partyId, blessing)
    }

    suspend fun remove(blessing: Blessing) {
        blessingCompendium.remove(partyId, blessing)
    }

    suspend fun saveMultipleBlessings(blessings: List<Blessing>) {
        blessingCompendium.saveItems(partyId, *blessings.toTypedArray())
    }

    suspend fun saveMultipleMiracles(miracles: List<Miracle>) {
        miracleCompendium.saveItems(partyId, *miracles.toTypedArray())
    }

    suspend fun save(trait: Trait) {
        traitCompendium.saveItems(partyId, trait)
    }

    suspend fun saveMultipleTraits(traits: List<Trait>) {
        traitCompendium.saveItems(partyId, *traits.toTypedArray())
    }

    suspend fun remove(trait: Trait) {
        traitCompendium.remove(partyId, trait)
    }

    suspend fun save(career: Career) {
        saveMultipleCareers(listOf(career))
    }

    suspend fun saveMultipleCareers(careers: List<Career>) {
        careerCompendium.saveItems(partyId, *careers.toTypedArray())
    }

    suspend fun remove(career: Career) {
        careerCompendium.remove(partyId, career)
    }

    suspend fun buildExportJson(): String {
        return coroutineScope {
            val skills = async { skills.first().map(SkillImport::fromSkill) }
            val talents = async { talents.first().map(TalentImport::fromTalent) }
            val spells = async { spells.first().map(SpellImport::fromSpell) }
            val blessings = async { blessings.first().map(BlessingImport::fromBlessing) }
            val miracles = async { miracles.first().map(MiracleImport::fromMiracle) }
            val traits = async { traits.first().map(TraitImport::fromTrait) }
            val careers = async { careers.first().map(CareerImport::fromCareer) }

            val bundle = CompendiumBundle(
                skills = skills.await(),
                talents = talents.await(),
                spells = spells.await(),
                blessings = blessings.await(),
                miracles = miracles.await(),
                traits = traits.await(),
                careers = careers.await(),
            )

            json.encodeToString(serializer(), bundle)
        }
    }

    companion object {
        private val json = Json {
            encodeDefaults = true
        }
    }
}
