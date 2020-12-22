package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.BodyPadding
import cz.muni.fi.rpg.viewModels.SkillsViewModel

@Composable
internal fun CompendiumSkillChooser(
    viewModel: SkillsViewModel,
    onSkillSelected: (Skill) -> Unit,
    onCustomSkillRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_choose_compendium_skill)) })
        }
    ) {
        val compendiumSkills = viewModel.notUsedSkillsFromCompendium.collectAsState(null).value
        val totalCompendiumSkillCount = viewModel.compendiumSkillsCount.collectAsState(null).value

        if (compendiumSkills == null || totalCompendiumSkillCount == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumSkills.isEmpty()) {
                    EmptyUI(
                        drawableResourceId = R.drawable.ic_skills,
                        textId = R.string.no_skills_in_compendium,
                        subTextId = if (totalCompendiumSkillCount == 0)
                            R.string.no_skills_in_compendium_sub_text_player
                        else null,
                    )
                } else {
                    LazyColumnFor(
                        items = compendiumSkills,
                        contentPadding = PaddingValues(BodyPadding),
                    ) { skill ->
                        ListItem(
                            modifier = Modifier.clickable(onClick = { onSkillSelected(skill) }),
                            icon = { ItemIcon(skill.characteristic.getIconId(), ItemIcon.Size.Small) },
                            text = { Text(skill.name) }
                        )
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth().padding(BodyPadding),
                onClick = onCustomSkillRequest,
            ) {
                Text(stringResource(R.string.button_add_non_compendium_skill))
            }
        }
    }
}