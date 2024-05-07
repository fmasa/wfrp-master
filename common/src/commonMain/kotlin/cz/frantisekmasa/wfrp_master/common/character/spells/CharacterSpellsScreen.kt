package cz.frantisekmasa.wfrp_master.common.character.spells

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.spells.add.AddSpellScreen
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CharacterSpellsScreen(
    characterId: CharacterId,
    state: SpellsScreenState,
    modifier: Modifier,
    onRemove: (Spell) -> Unit,
) {
    val navigation = LocalNavigationTransaction.current

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { navigation.navigate(AddSpellScreen(characterId)) }) {
                Icon(
                    Icons.Rounded.Add,
                    stringResource(Str.spells_title_add),
                )
            }
        },
    ) {
        MainContainer(
            characterId = characterId,
            spells = state.spells,
            onRemove = onRemove,
        )
    }
}

@Composable
private fun MainContainer(
    characterId: CharacterId,
    spells: ImmutableList<Spell>,
    onRemove: (Spell) -> Unit,
) {
    val spellsByLore =
        remember(spells) {
            spells.groupBy { it.lore }
                .asSequence()
                .sortedBy { it.key?.ordinal ?: SpellLore.values().size }
                .toList()
        }

    if (spellsByLore.isEmpty()) {
        EmptyUI(
            text = stringResource(Str.spells_messages_character_has_no_spell),
            subText = stringResource(Str.spells_messages_character_has_no_spell_subtext),
            icon = Resources.Drawable.Spell,
        )
        return
    }

    LazyColumn(
        contentPadding =
            PaddingValues(
                bottom = Spacing.bottomPaddingUnderFab,
                start = Spacing.small,
                end = Spacing.small,
            ),
    ) {
        items(spellsByLore) { (lore, spells) ->
            CardContainer(Modifier.padding(top = Spacing.small)) {
                Column {
                    CardTitle(
                        buildString {
                            append(lore?.localizedName ?: stringResource(Str.spells_lores_other))

                            lore?.wind?.let {
                                append(" ($it)")
                            }
                        },
                    )

                    val navigation = LocalNavigationTransaction.current

                    spells.forEachIndexed { index, spell ->
                        key(spell.id, index) {
                            SpellItem(
                                spell = spell,
                                onClick = {
                                    navigation.navigate(
                                        CharacterSpellDetailScreen(characterId, spell.id),
                                    )
                                },
                                onRemove = { onRemove(spell) },
                                isLast = index == spells.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellItem(
    spell: Spell,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    isLast: Boolean,
) {
    CardItem(
        name = spell.name,
        onClick = onClick,
        showDivider = !isLast,
        contextMenuItems = listOf(ContextMenu.Item(stringResource(Str.common_ui_button_remove), onRemove)),
        badge = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.tiny),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Text(stringResource(Str.spells_casting_number_shortcut))
                    Text(
                        spell.effectiveCastingNumber.toString(),
                        Modifier.padding(start = Spacing.tiny),
                    )
                }

                if (spell.memorized) {
                    Icon(
                        drawableResource(Resources.Drawable.MemorizeSpell),
                        stringResource(Str.spells_label_memorized),
                        Modifier.size(16.dp),
                    )
                }
            }
        },
    )
}
