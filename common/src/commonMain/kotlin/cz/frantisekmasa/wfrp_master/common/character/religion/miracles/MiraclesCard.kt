package cz.frantisekmasa.wfrp_master.common.character.religion.miracles

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
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog.AddMiracleDialog
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog.EditMiracleDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.launch


@Composable
internal fun MiraclesCard(screenModel: MiraclesScreenModel) {
    val miracles = screenModel.items.collectWithLifecycle(null).value ?: return
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
                var editedMiracleId: Uuid? by rememberSaveable { mutableStateOf(null) }

                for (miracle in miracles) {
                    MiracleItem(
                        miracle,
                        onClick = { editedMiracleId = miracle.id },
                        onRemove = { coroutineScope.launch { screenModel.removeItem(miracle) } },
                    )
                }

                editedMiracleId?.let { miracleId ->
                    EditMiracleDialog(
                        screenModel = screenModel,
                        miracleId = miracleId,
                        onDismissRequest = { editedMiracleId = null }
                    )
                }
            }

            var showAddMiracleDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(strings.titleAdd, onClick = { showAddMiracleDialog = true })

            if (showAddMiracleDialog) {
                AddMiracleDialog(
                    screenModel = screenModel,
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
            ContextMenu.Item(
                LocalStrings.current.commonUi.buttonRemove,
                onClick = { onRemove() },
            )
        ),
    )
}
