package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
internal fun TalentsCard(
    viewModel: TalentsViewModel,
    onClick: (Talent) -> Unit,
    onRemove: (Talent) -> Unit,
    onAddButtonClicked: () -> Unit,
) {
    val talents = viewModel.talents.observeAsState().value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_talents)

            if (talents.isNotEmpty()) {
                Column {
                    for (talent in talents) {
                        TalentItem(
                            talent,
                            onClick = { onClick(talent) },
                            onRemove = { onRemove(talent) }
                        )
                    }
                }
            }

            CardButton(R.string.title_talent_add, onClick = onAddButtonClicked)
        }
    }
}

@Composable
private fun TalentItem(talent: Talent, onClick: () -> Unit, onRemove: () -> Unit) {
    CardItem(
        name = talent.name,
        description = talent.description,
        iconRes = R.drawable.ic_skills,
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove() })
        ),
        badgeContent = { Text("+ ${talent.taken}") }
    )
}