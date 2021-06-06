package cz.frantisekmasa.wfrp_master.religion.ui.blessings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.*
import cz.frantisekmasa.wfrp_master.religion.R
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import kotlinx.coroutines.launch
import java.util.*

@Composable
internal fun BlessingsCard(viewModel: BlessingsViewModel) {
    val blessings = viewModel.items.observeAsState().value ?: return
    val coroutineScope = rememberCoroutineScope()

    CardContainer(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_blessings)

            if (blessings.isEmpty()) {
                EmptyUI(
                    R.string.no_blessings,
                    R.drawable.ic_pray,
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

            CardButton(R.string.title_blessing_add, onClick = { showAddBlessingDialog = true })

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
        R.drawable.ic_pray,
        onClick = onClick,
        listOf(ContextMenu.Item(stringResource(R.string.button_remove), onClick = { onRemove() })),
    )
}
