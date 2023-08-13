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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.AddTraitDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun TraitsCard(
    screenModel: TraitsScreenModel,
    onRemove: (Trait) -> Unit,
) {
    val traits = screenModel.items.collectWithLifecycle(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(stringResource(Str.traits_title_traits))

            if (traits.isNotEmpty()) {
                Column {
                    val navigation = LocalNavigationTransaction.current

                    for (trait in traits) {
                        TraitItem(
                            trait,
                            onClick = {
                                navigation.navigate(
                                    CharacterTraitDetailScreen(
                                        screenModel.characterId,
                                        trait.id,
                                    )
                                )
                            },
                            onRemove = { onRemove(trait) }
                        )
                    }
                }
            }

            var showAddTraitDialog by rememberSaveable { mutableStateOf(false) }

            CardButton(
                stringResource(Str.traits_title_add),
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
            ContextMenu.Item(stringResource(Str.common_ui_button_remove), onClick = { onRemove() })
        ),
    )
}
