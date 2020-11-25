package cz.muni.fi.rpg.ui.character.talents.dialog

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
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.Talent
import cz.muni.fi.rpg.ui.common.composables.BodyPadding
import cz.muni.fi.rpg.ui.common.composables.EmptyUI
import cz.muni.fi.rpg.ui.common.composables.FullScreenProgress
import cz.muni.fi.rpg.ui.common.composables.ItemIcon
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
internal fun CompendiumTalentChooser(
    viewModel: TalentsViewModel,
    onTalentSelected: (Talent) -> Unit,
    onCustomTalentRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_choose_compendium_talent)) })
        }
    ) {
        val compendiumTalents = viewModel.notUsedTalentsFromCompendium.collectAsState(null).value
        val totalCompendiumTalentCount = viewModel.compendiumTalentsCount.collectAsState(null).value

        if (compendiumTalents == null || totalCompendiumTalentCount == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumTalents.isEmpty()) {
                    EmptyUI(
                        drawableResourceId = R.drawable.ic_skills,
                        textId = R.string.no_talents_in_compendium,
                        subTextId = if (totalCompendiumTalentCount == 0)
                            R.string.no_talents_in_compendium_sub_text_player
                        else null,
                    )
                } else {
                    LazyColumnFor(
                        items = compendiumTalents,
                        contentPadding = PaddingValues(BodyPadding),
                    ) { talent ->
                        ListItem(
                            modifier = Modifier.clickable(onClick = { onTalentSelected(talent) }),
                            icon = { ItemIcon(R.drawable.ic_skills, ItemIcon.Size.Small) },
                            text = { Text(talent.name) }
                        )
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth().padding(BodyPadding),
                onClick = onCustomTalentRequest,
            ) {
                Text(stringResource(R.string.button_add_non_compendium_talent))
            }
        }
    }
}