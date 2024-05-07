package cz.frantisekmasa.wfrp_master.common.character.traits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.traits.add.AddTraitScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.StickyHeader
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.traitsCard(
    characterId: CharacterId,
    traits: ImmutableList<Trait>,
    onRemove: (Trait) -> Unit,
) {
    stickyHeader(key = "traits-header") {
        StickyHeader {
            CardTitle(
                stringResource(Str.traits_title_traits),
                actions = {
                    val navigation = LocalNavigationTransaction.current
                    IconButton(
                        onClick = { navigation.navigate(AddTraitScreen(characterId)) },
                    ) {
                        Icon(Icons.Rounded.Add, stringResource(Str.traits_title_add))
                    }
                },
            )
        }
    }

    itemsIndexed(
        traits,
        contentType = { _, _ -> "skill" },
        key = { _, it -> "trait" to it.id },
    ) { index, trait ->
        val navigation = LocalNavigationTransaction.current

        TraitItem(
            trait,
            onClick = {
                navigation.navigate(
                    CharacterTraitDetailScreen(characterId, trait.id),
                )
            },
            onRemove = { onRemove(trait) },
            showDivider = index != 0,
        )
    }
}

@Composable
private fun TraitItem(
    trait: Trait,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    showDivider: Boolean,
) {
    Column(Modifier.padding(horizontal = Spacing.large)) {
        if (showDivider) {
            Divider()
        }

        CardItem(
            name = derivedStateOf { trait.evaluatedName }.value,
            onClick = onClick,
            contextMenuItems =
                listOf(
                    ContextMenu.Item(stringResource(Str.common_ui_button_remove), onClick = { onRemove() }),
                ),
            showDivider = false,
        )
    }
}
