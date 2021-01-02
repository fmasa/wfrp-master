package cz.muni.fi.rpg.ui.gameMaster.rolls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.BodyPadding
import cz.muni.fi.rpg.viewModels.SkillTestViewModel


@Composable
internal fun SkillChooser(
    viewModel: SkillTestViewModel,
    onDismissRequest: () -> Unit,
    onSkillSelected: (Skill) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onDismissRequest) },
                title = { Text(stringResource(R.string.title_skill_select)) },
            )
        }
    ) {
        val skills = viewModel.skills.collectAsState(null).value

        if (skills == null) {
            FullScreenProgress()
            return@Scaffold
        }

        if (skills.isEmpty()) {
            EmptyUI(
                drawableResourceId = R.drawable.ic_skills,
                textId = R.string.no_skills_in_compendium,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(BodyPadding),
                modifier = Modifier.clipToBounds(),
            ) {
                items(skills) { skill ->
                    ListItem(
                        modifier = Modifier.clickable(onClick = { onSkillSelected(skill) }),
                        icon = { ItemIcon(skill.characteristic.getIconId(), ItemIcon.Size.Small) },
                        text = { Text(skill.name) }
                    )
                }
            }
        }
    }
}