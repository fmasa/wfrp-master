package cz.frantisekmasa.wfrp_master.religion.ui.blessings

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
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun BlessingsCard(viewModel: BlessingsViewModel) {
    val blessings = viewModel.items.collectWithLifecycle(null).value ?: return
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
                var editedBlessingId: UUID? by rememberSaveable { mutableStateOf(null) }

                for (blessing in blessings) {
                    BlessingItem(
                        blessing,
                        onClick = { editedBlessingId = blessing.id },
                        onRemove = { coroutineScope.launch { viewModel.removeItem(blessing) } },
                    )
                }

                editedBlessingId?.let { blessingId ->
                    EditBlessingDialog(
                        viewModel = viewModel,
                        blessingId = blessingId,
                        onDismissRequest = { editedBlessingId = null }
                    )
                }
            }

            var showAddBlessingDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(strings.titleAdd, onClick = { showAddBlessingDialog = true })

            if (showAddBlessingDialog) {
                AddBlessingDialog(
                    viewModel = viewModel,
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
        blessing.name,
        blessing.effect,
        icon = { ItemIcon(Resources.Drawable.Blessing, ItemIcon.Size.Small) },
        onClick = onClick,
        listOf(
            ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onClick = { onRemove() }),
        ),
    )
}
