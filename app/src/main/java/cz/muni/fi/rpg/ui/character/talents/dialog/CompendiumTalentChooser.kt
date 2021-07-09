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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
internal fun CompendiumTalentChooser(
    viewModel: TalentsViewModel,
    onTalentSelected: (Talent) -> Unit,
    onCustomTalentRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(stringResource(R.string.title_choose_compendium_talent)) }
            )
        }
    ) {
        val compendiumTalents = viewModel.notUsedTalentsFromCompendium.observeAsState().value
        val totalCompendiumTalentCount = viewModel.compendiumTalentsCount.observeAsState().value

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
                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        items(compendiumTalents) { talent ->
                            ListItem(
                                modifier = Modifier.clickable(onClick = { onTalentSelected(talent) }),
                                icon = { ItemIcon(R.drawable.ic_skills, ItemIcon.Size.Small) },
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
                Text(stringResource(R.string.button_add_non_compendium_talent))
            }
        }
    }
}
