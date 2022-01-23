package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.muni.fi.rpg.viewModels.SkillsViewModel

@Composable
internal fun CompendiumSkillChooser(
    viewModel: SkillsViewModel,
    onSkillSelected: (Skill) -> Unit,
    onCustomSkillRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val strings = LocalStrings.current.skills

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(strings.titleChooseCompendiumSkill) },
            )
        }
    ) {
        val compendiumSkills = viewModel.notUsedSkillsFromCompendium.collectWithLifecycle(null).value
        val totalCompendiumSkillCount = viewModel.compendiumSkillsCount.collectWithLifecycle(null).value

        if (compendiumSkills == null || totalCompendiumSkillCount == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumSkills.isEmpty()) {
                    EmptyUI(
                        icon = Resources.Drawable.Skill,
                        text = strings.messages.noSkillsInCompendium,
                        subText = when (totalCompendiumSkillCount) {
                            0 -> strings.messages.noSkillsInCompendiumSubtextPlayer
                            else -> null
                        },
                    )
                } else {
                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        items(compendiumSkills) { skill ->
                            key(skill.id) {
                                ListItem(
                                    modifier = Modifier.clickable(onClick = { onSkillSelected(skill) }),
                                    icon = { ItemIcon(skill.characteristic.getIcon(), ItemIcon.Size.Small) },
                                    text = { Text(skill.name) }
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.bodyPadding),
                onClick = onCustomSkillRequest,
            ) {
                Text(strings.buttonAddNonCompendium)
            }
        }
    }
}
