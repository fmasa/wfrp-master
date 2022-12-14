package cz.frantisekmasa.wfrp_master.common.character.religion.blessings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog.AddBlessingDialog
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog.EditBlessingDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.launch

@Composable
internal fun BlessingsCard(screenModel: BlessingsScreenModel) {
    val blessings = screenModel.items.collectWithLifecycle(null).value ?: return
    val coroutineScope = rememberCoroutineScope()

    val strings = LocalStrings.current.blessings

    CardContainer(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    ) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(strings.title)

            if (blessings.isEmpty()) {
                EmptyUI(
                    strings.messages.characterHasNoBlessings,
                    Resources.Drawable.Blessing,
                    size = EmptyUI.Size.Small
                )
            } else {
                var editedBlessingId: Uuid? by rememberSaveable { mutableStateOf(null) }

                for (blessing in blessings) {
                    BlessingItem(
                        blessing,
                        onClick = { editedBlessingId = blessing.id },
                        onRemove = { coroutineScope.launch { screenModel.removeItem(blessing) } },
                    )
                }

                editedBlessingId?.let { blessingId ->
                    EditBlessingDialog(
                        screenModel = screenModel,
                        blessingId = blessingId,
                        onDismissRequest = { editedBlessingId = null }
                    )
                }
            }

            var showAddBlessingDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(strings.titleAdd, onClick = { showAddBlessingDialog = true })

            if (showAddBlessingDialog) {
                AddBlessingDialog(
                    screenModel = screenModel,
                    onDismissRequest = { showAddBlessingDialog = false }
                )
            }
        }
    }
}

@Composable
private fun BlessingItem(
    blessing: Blessing,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    CardItem(
        name = blessing.name,
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onClick = { onRemove() }),
        ),
    )
}
