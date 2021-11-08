package cz.muni.fi.rpg.ui.character.talents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.character.talents.dialog.AddTalentDialog
import cz.muni.fi.rpg.ui.character.talents.dialog.EditTalentDialog
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import java.util.UUID

@Composable
internal fun TalentsCard(
    viewModel: TalentsViewModel,
    onRemove: (Talent) -> Unit,
) {
    val talents = viewModel.talents.collectWithLifecycle(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_talents)

            if (talents.isNotEmpty()) {
                Column {
                    var editedTalentId: UUID? by rememberSaveable { mutableStateOf(null) }

                    for (talent in talents) {
                        TalentItem(
                            talent,
                            onClick = { editedTalentId = talent.id },
                            onRemove = { onRemove(talent) }
                        )
                    }

                    editedTalentId?.let {
                        EditTalentDialog(
                            viewModel = viewModel,
                            talentId = it,
                            onDismissRequest = { editedTalentId = null },
                        )
                    }
                }
            }

            var showAddTalentDialog by rememberSaveable { mutableStateOf(false) }

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
        badge = { Text("+ ${talent.taken}") }
    )
}
