package cz.muni.fi.rpg.ui.character.talents.dialog

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
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
internal fun CompendiumTalentChooser(
    viewModel: TalentsViewModel,
    onTalentSelected: (Talent) -> Unit,
    onCustomTalentRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val strings = LocalStrings.current.talents

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(strings.titleChooseCompendiumTalent) }
            )
        }
    ) {
        val compendiumTalents = viewModel.notUsedTalentsFromCompendium.collectWithLifecycle(null).value
        val totalCompendiumTalentCount = viewModel.compendiumTalentsCount.collectWithLifecycle(null).value

        if (compendiumTalents == null || totalCompendiumTalentCount == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumTalents.isEmpty()) {
                    EmptyUI(
                        icon = Resources.Drawable.Skill,
                        text = strings.messages.noTalentsInCompendium,
                        subText = when (totalCompendiumTalentCount) {
                            0 -> strings.messages.noTalentsInCompendiumSubtextPlayer
                            else -> null
                        },
                    )
                } else {
                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        items(compendiumTalents) { talent ->
                            ListItem(
                                modifier = Modifier.clickable(onClick = { onTalentSelected(talent) }),
                                icon = { ItemIcon(Resources.Drawable.Skill, ItemIcon.Size.Small) },
                                text = { Text(talent.name) }
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.bodyPadding),
                onClick = onCustomTalentRequest,
            ) {
                Text(strings.buttonAddNonCompendium)
            }
        }
    }
}
