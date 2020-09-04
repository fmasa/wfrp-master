package cz.muni.fi.rpg.ui.character.skills

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillCharacteristic
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel


@Composable
internal fun SkillsCard(
    characterVm: CharacterViewModel,
    skillsVm: SkillsViewModel,
    onClick: (Skill) -> Unit,
    onRemove: (Skill) -> Unit,
    onNewSkillButtonClicked: () -> Unit,
) {
    val skills = skillsVm.skills.observeAsState().value ?: return
    val characteristics = characterVm.character.right()
        .observeAsState().value?.getCharacteristics() ?: return


    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_skills)

            if (skills.isEmpty()) {
                EmptyUI(R.string.no_skills, R.drawable.ic_skills, EmptyUI.Size.Small)
            } else {
                LazyColumnFor(skills) { skill ->
                    SkillItem(
                        skill,
                        characteristics,
                        onClick = { onClick(skill) },
                        onRemove = { onRemove(skill) }
                    )
                }
            }

            CardButton(R.string.title_addSkill, onClick = onNewSkillButtonClicked)
        }
    }
}

@Composable
private fun SkillItem(
    skill: Skill,
    characteristics: Stats,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    CardItem(
        skill.name,
        skill.description,
        resolveSkillIcon(skill),
        onClick = onClick,
        listOf(ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove() })),
        badgeContent = { TestNumber(skill, characteristics) }
    )
}

@Composable
private fun TestNumber(skill: Skill, characteristics: Stats) {

    val testNumber = skill.advances + when (skill.characteristic) {
        SkillCharacteristic.AGILITY -> characteristics.agility
        SkillCharacteristic.BALLISTIC_SKILL -> characteristics.ballisticSkill
        SkillCharacteristic.DEXTERITY -> characteristics.dexterity
        SkillCharacteristic.FELLOWSHIP -> characteristics.fellowship
        SkillCharacteristic.INITIATIVE -> characteristics.initiative
        SkillCharacteristic.INTELLIGENCE -> characteristics.intelligence
        SkillCharacteristic.STRENGTH -> characteristics.strength
        SkillCharacteristic.TOUGHNESS -> characteristics.toughness
        SkillCharacteristic.WEAPON_SKILL -> characteristics.weaponSkill
        SkillCharacteristic.WILL_POWER -> characteristics.willPower
    }

    Row {
        Text(stringResource(R.string.skill_test_number_shortcut))
        Text(testNumber.toString(), Modifier.padding(start = 4.dp))
    }
}

@DrawableRes
private fun resolveSkillIcon(skill: Skill) = when (skill.characteristic) {
    SkillCharacteristic.AGILITY -> R.drawable.ic_agility
    SkillCharacteristic.BALLISTIC_SKILL -> R.drawable.ic_ballistic_skill
    SkillCharacteristic.DEXTERITY -> R.drawable.ic_dexterity
    SkillCharacteristic.INITIATIVE -> R.drawable.ic_initiative
    SkillCharacteristic.INTELLIGENCE -> R.drawable.ic_intelligence
    SkillCharacteristic.FELLOWSHIP -> R.drawable.ic_fellowship
    SkillCharacteristic.STRENGTH -> R.drawable.ic_strength
    SkillCharacteristic.TOUGHNESS -> R.drawable.ic_toughness
    SkillCharacteristic.WEAPON_SKILL -> R.drawable.ic_weapon_skill
    SkillCharacteristic.WILL_POWER -> R.drawable.ic_will_power
}