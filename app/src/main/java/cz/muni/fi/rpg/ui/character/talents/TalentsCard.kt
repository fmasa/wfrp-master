package cz.muni.fi.rpg.ui.character.talents

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.character.talents.dialog.AddTalentDialog
import cz.muni.fi.rpg.ui.character.talents.dialog.EditTalentDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
internal fun TalentsCard(
    viewModel: TalentsViewModel,
    onRemove: (Talent) -> Unit,
) {
    val talents = viewModel.talents.collectAsState(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_talents)

            if (talents.isNotEmpty()) {
                Column {
                    var editedTalent: Talent? by savedInstanceState { null }

                    for (talent in talents) {
                        TalentItem(
                            talent,
                            onClick = { editedTalent = talent },
                            onRemove = { onRemove(talent) }
                        )
                    }
                    
                    editedTalent?.let { 
                        EditTalentDialog(
                            viewModel = viewModel,
                            talent = it,
                            onDismissRequest = { editedTalent = null },
                        )
                    }
                }
            }

            var showAddTalentDialog by savedInstanceState { false }
            
            CardButton(
                R.string.title_talent_add,
                onClick = { showAddTalentDialog = true }
            )
            
            if (showAddTalentDialog) {
                AddTalentDialog(
                    viewModel = viewModel,
                    onDismissRequest = { showAddTalentDialog = false },
                )
            }
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
