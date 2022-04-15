package cz.frantisekmasa.wfrp_master.common.compendium

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow


class CompendiumScreenModel(
    private val partyId: PartyId,
    private val skillCompendium: Compendium<Skill>,
    private val talentsCompendium: Compendium<Talent>,
    private val spellCompendium: Compendium<Spell>,
    private val blessingCompendium: Compendium<Blessing>,
    private val miracleCompendium: Compendium<Miracle>,
    parties: PartyRepository,
) : ScreenModel {

    val party: Flow<Party> = parties.getLive(partyId).right()
    val skills: Flow<List<Skill>> = skillCompendium.liveForParty(partyId)
    val talents: Flow<List<Talent>> = talentsCompendium.liveForParty(partyId)
    val spells: Flow<List<Spell>> = spellCompendium.liveForParty(partyId)
    val blessings: Flow<List<Blessing>> = blessingCompendium.liveForParty(partyId)
    val miracles: Flow<List<Miracle>> = miracleCompendium.liveForParty(partyId)

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
}
