package cz.frantisekmasa.wfrp_master.common.character.talents

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
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.AddTalentDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
internal fun TalentsCard(
    screenModel: TalentsScreenModel,
    onRemove: (Talent) -> Unit,
) {
    val talents = screenModel.items.collectWithLifecycle(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            val strings = LocalStrings.current.talents

            CardTitle(strings.titleTalents)

            if (talents.isNotEmpty()) {
                Column {
                    val navigation = LocalNavigationTransaction.current

                    for (talent in talents) {
                        TalentItem(
                            talent,
                            onClick = {
                                navigation.navigate(
                                    CharacterTalentDetailScreen(
                                        screenModel.characterId,
                                        talent.id,
                                    )
                                )
                            },
                            onRemove = { onRemove(talent) }
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
                    screenModel = screenModel,
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
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onClick = { onRemove() })
        ),
        badge = { Text("+ ${talent.taken}") }
    )
}
