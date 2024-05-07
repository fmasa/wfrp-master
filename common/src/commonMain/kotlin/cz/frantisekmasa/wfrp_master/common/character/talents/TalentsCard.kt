package cz.frantisekmasa.wfrp_master.common.character.talents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.talents.add.AddTalentScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.StickyHeader
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.talentsCard(
    characterId: CharacterId,
    talents: ImmutableList<Talent>,
    onRemove: (Talent) -> Unit,
) {
    stickyHeader(key = "talents-header") {
        StickyHeader {
            CardTitle(
                stringResource(Str.talents_title_talents),
                actions = {
                    val navigation = LocalNavigationTransaction.current
                    IconButton(
                        onClick = { navigation.navigate(AddTalentScreen(characterId)) },
                    ) {
                        Icon(Icons.Rounded.Add, stringResource(Str.talents_title_add))
                    }
                },
            )
        }
    }

    itemsIndexed(talents, key = { _, it -> "talent" to it.id }) { index, talent ->
        val navigation = LocalNavigationTransaction.current

        TalentItem(
            talent,
            onClick = {
                navigation.navigate(
                    CharacterTalentDetailScreen(
                        characterId,
                        talent.id,
                    ),
                )
            },
            onRemove = { onRemove(talent) },
            showDivider = index != 0,
        )
    }
}

@Composable
private fun TalentItem(
    talent: Talent,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    showDivider: Boolean,
) {
    Column(Modifier.padding(horizontal = Spacing.large)) {
        if (showDivider) {
            Divider()
        }

        CardItem(
            name = talent.name,
            onClick = onClick,
            contextMenuItems =
                listOf(
                    ContextMenu.Item(stringResource(Str.common_ui_button_remove), onClick = { onRemove() }),
                ),
            badge = { Text("+ ${talent.taken}") },
            showDivider = false,
        )
    }
}
