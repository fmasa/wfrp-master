package cz.frantisekmasa.wfrp_master.common.character.traits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.traits.add.AddTraitScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TraitsCard(
    characterId: CharacterId,
    traits: ImmutableList<Trait>,
    onRemove: (Trait) -> Unit,
) {
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
                                    CharacterTraitDetailScreen(characterId, trait.id)
                                )
                            },
                            onRemove = { onRemove(trait) }
                        )
                    }
                }
            }

            val navigation = LocalNavigationTransaction.current

            CardButton(
                stringResource(Str.traits_title_add),
                onClick = { navigation.navigate(AddTraitScreen(characterId)) },
            )
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
