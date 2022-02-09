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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu.Item
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
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
            val strings = LocalStrings.current.miracles

            CardTitle(strings.title)

            if (miracles.isEmpty()) {
                EmptyUI(
                    strings.messages.characterHasNoMiracles,
                    Resources.Drawable.Miracle,
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

            CardButton(strings.titleAdd, onClick = { showAddMiracleDialog = true })

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
        { ItemIcon(Resources.Drawable.Miracle, ItemIcon.Size.Small) },
        onClick = onClick,
        listOf(
            Item(
                LocalStrings.current.commonUi.buttonRemove,
                onClick = { onRemove() },
            )
        ),
    )
}
