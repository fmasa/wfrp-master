package cz.frantisekmasa.wfrp_master.common.character.religion.blessings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.add.AddBlessingScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun BlessingsCard(
    characterId: CharacterId,
    blessings: ImmutableList<Blessing>,
    onRemove: (Blessing) -> Unit,
) {
    CardContainer(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
    ) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(stringResource(Str.blessings_title))

            if (blessings.isEmpty()) {
                EmptyUI(
                    stringResource(Str.blessings_messages_character_has_no_blessings),
                    Resources.Drawable.Blessing,
                    size = EmptyUI.Size.Small,
                )
            } else {
                val navigation = LocalNavigationTransaction.current

                for (blessing in blessings) {
                    BlessingItem(
                        blessing,
                        onClick = {
                            navigation.navigate(
                                CharacterBlessingDetailScreen(characterId, blessing.id),
                            )
                        },
                        onRemove = { onRemove(blessing) },
                    )
                }
            }

            val navigation = LocalNavigationTransaction.current

            CardButton(
                stringResource(Str.blessings_title_add),
                onClick = { navigation.navigate(AddBlessingScreen(characterId)) },
            )
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
        contextMenuItems =
            listOf(
                ContextMenu.Item(stringResource(Str.common_ui_button_remove), onClick = { onRemove() }),
            ),
    )
}
