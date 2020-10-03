package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
fun CharacterSkillsScreen(
    modifier: Modifier = Modifier,
    skillsVm: SkillsViewModel,
    talentsVm: TalentsViewModel,
    characterVm: CharacterViewModel,
    onSkillDialogRequest: (Skill?) -> Unit,
    onTalentDialogRequest: (Talent?) -> Unit,
) {
    ScrollableColumn(modifier.background(MaterialTheme.colors.background)) {
        SkillsCard(
            characterVm,
            skillsVm,
            onClick = { onSkillDialogRequest(it) },
            onRemove = { skillsVm.removeSkill(it) },
            onNewSkillButtonClicked = { onSkillDialogRequest(null) },
        )

        TalentsCard(
            talentsVm,
            onClick = { onTalentDialogRequest(it) },
            onRemove = { talentsVm.removeTalent(it) },
            onAddButtonClicked = { onTalentDialogRequest(null) }
        )
    }
}
