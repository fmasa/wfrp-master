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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.character.talents.dialog.AddTalentDialog
import cz.muni.fi.rpg.ui.character.talents.dialog.EditTalentDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
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
            val strings = LocalStrings.current.talents

            CardTitle(strings.titleTalents)

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
                strings.titleAdd,
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
        icon = { ItemIcon(Resources.Drawable.Skill, ItemIcon.Size.Small) },
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onClick = { onRemove() })
        ),
        badge = { Text("+ ${talent.taken}") }
    )
}
