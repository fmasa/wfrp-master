package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu.Item
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import cz.muni.fi.rpg.R
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun MiraclesCard(viewModel: MiraclesViewModel) {
    val miracles = viewModel.items.collectWithLifecycle(null).value ?: return
    val coroutineScope = rememberCoroutineScope()

    CardContainer(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    ) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_miracles)

            if (miracles.isEmpty()) {
                EmptyUI(
                    R.string.no_miracles,
                    R.drawable.ic_pray,
                    size = EmptyUI.Size.Small
                )
            } else {
                var editedMiracleId: UUID? by rememberSaveable { mutableStateOf(null) }

                for (miracle in miracles) {
                    MiracleItem(
                        miracle,
                        onClick = { editedMiracleId = miracle.id },
                        onRemove = { coroutineScope.launch { viewModel.removeItem(miracle) } },
                    )
                }

                editedMiracleId?.let { miracleId ->
                    EditMiracleDialog(
                        viewModel = viewModel,
                        miracleId = miracleId,
                        onDismissRequest = { editedMiracleId = null }
                    )
                }
            }

            var showAddMiracleDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(R.string.title_miracle_add, onClick = { showAddMiracleDialog = true })

            if (showAddMiracleDialog) {
                AddMiracleDialog(
                    viewModel = viewModel,
                    onDismissRequest = { showAddMiracleDialog = false }
                )
            }
        }
    }
}

@Composable
private fun MiracleItem(
    miracle: Miracle,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    CardItem(
        miracle.name,
        miracle.effect,
        R.drawable.ic_pray,
        onClick = onClick,
        listOf(Item(stringResource(R.string.button_remove), onClick = { onRemove() })),
    )
}
