package cz.frantisekmasa.wfrp_master.common.character.talents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.talents.add.AddTalentScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TalentsCard(
    characterId: CharacterId,
    talents: ImmutableList<Talent>,
    onRemove: (Talent) -> Unit,
) {
    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(stringResource(Str.talents_title_talents))

            if (talents.isNotEmpty()) {
                Column {
                    val navigation = LocalNavigationTransaction.current

                    for (talent in talents) {
                        TalentItem(
                            talent,
                            onClick = {
                                navigation.navigate(
                                    CharacterTalentDetailScreen(
                                        characterId,
                                        talent.id,
                                    )
                                )
                            },
                            onRemove = { onRemove(talent) }
                        )
                    }
                }
            }

            val navigation = LocalNavigationTransaction.current

            CardButton(
                stringResource(Str.talents_title_add),
                onClick = { navigation.navigate(AddTalentScreen(characterId)) },
            )
        }
    }
}

@Composable
private fun TalentItem(talent: Talent, onClick: () -> Unit, onRemove: () -> Unit) {
    CardItem(
        name = talent.name,
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(stringResource(Str.common_ui_button_remove), onClick = { onRemove() })
        ),
        badge = { Text("+ ${talent.taken}") }
    )
}
