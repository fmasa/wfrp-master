package cz.frantisekmasa.wfrp_master.common.character.traits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.AddTraitDialog
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.EditTraitDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
internal fun TraitsCard(
    screenModel: TraitsScreenModel,
    onRemove: (Trait) -> Unit,
) {
    val traits = screenModel.items.collectWithLifecycle(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            val strings = LocalStrings.current.traits

            CardTitle(strings.titleTraits)

            if (traits.isNotEmpty()) {
                Column {
                    var editedTraitId: Uuid? by rememberSaveable { mutableStateOf(null) }

                    for (trait in traits) {
                        TraitItem(
                            trait,
                            onClick = { editedTraitId = trait.id },
                            onRemove = { onRemove(trait) }
                        )
                    }

                    editedTraitId?.let {
                        EditTraitDialog(
                            screenModel = screenModel,
                            traitId = it,
                            onDismissRequest = { editedTraitId = null },
                        )
                    }
                }
            }

            var showAddTraitDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(
                strings.titleAdd,
                onClick = { showAddTraitDialog = true },
            )

            if (showAddTraitDialog) {
                AddTraitDialog(
                    screenModel = screenModel,
                    onDismissRequest = { showAddTraitDialog = false },
                )
            }
        }
    }
}

@Composable
private fun TraitItem(trait: Trait, onClick: () -> Unit, onRemove: () -> Unit) {
    CardItem(
        name = derivedStateOf { trait.evaluatedName }.value,
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onClick = { onRemove() })
        ),
    )
}
