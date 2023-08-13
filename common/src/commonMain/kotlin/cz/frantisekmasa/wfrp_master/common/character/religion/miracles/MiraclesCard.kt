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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog.AddMiracleDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import dev.icerock.moko.resources.compose.stringResource
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
            CardTitle(stringResource(Str.miracles_title))

            if (miracles.isEmpty()) {
                EmptyUI(
                    stringResource(Str.miracles_messages_character_has_no_miracles),
                    Resources.Drawable.Miracle,
                    size = EmptyUI.Size.Small
                )
            } else {
                val navigation = LocalNavigationTransaction.current

                for (miracle in miracles) {
                    MiracleItem(
                        miracle,
                        onClick = {
                            navigation.navigate(
                                CharacterMiracleDetailScreen(
                                    screenModel.characterId,
                                    miracle.id,
                                )
                            )
                        },
                        onRemove = { coroutineScope.launch { screenModel.removeItem(miracle) } },
                    )
                }
            }

            var showAddMiracleDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(
                stringResource(Str.miracles_title_add).uppercase(),
                onClick = { showAddMiracleDialog = true },
            )

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
        name = miracle.name,
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(
                stringResource(Str.common_ui_button_remove),
                onClick = { onRemove() },
            )
        ),
    )
}
