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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.AddSpellDialog
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.EditSpellDialog
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CharacterSpellsScreen(
    screenModel: SpellsScreenModel,
    modifier: Modifier,
) {
    var showAddSpellDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSpellDialog = true }) {
                Icon(
                    Icons.Rounded.Add,
                    LocalStrings.current.spells.titleAdd,
                )
            }
        }
    ) {
        MainContainer(screenModel)

        if (showAddSpellDialog) {
            AddSpellDialog(
                screenModel = screenModel,
                onDismissRequest = { showAddSpellDialog = false },
            )
        }
    }
}

@Composable
private fun MainContainer(screenModel: SpellsScreenModel) {
    val allSpells = screenModel.items.collectWithLifecycle(null).value ?: return
    val spellsByLore = remember(allSpells) {
        allSpells.groupBy { it.lore }
            .asSequence()
            .sortedBy { it.key?.ordinal ?: SpellLore.values().size }
            .toList()
    }

    if (spellsByLore.isEmpty()) {
        val messages = LocalStrings.current.spells.messages

        EmptyUI(
            text = messages.characterHasNoSpell,
            subText = messages.characterHasNoSpellSubtext,
            icon = Resources.Drawable.Spell,
        )
        return
    }

    var editedSpellId: Uuid? by rememberSaveable { mutableStateOf(null) }

    LazyColumn(
        contentPadding = PaddingValues(
            bottom = Spacing.bottomPaddingUnderFab,
            start = Spacing.small,
            end = Spacing.small,
        )
    ) {
        items(spellsByLore) { (lore, spells) ->
            CardContainer(Modifier.padding(top = Spacing.small)) {
                Column {
                    CardTitle(
                        buildString {
                            append(lore?.localizedName ?: LocalStrings.current.spells.lores.other)

                            lore?.wind?.let {
                                append(" ($it)")
                            }
                        }
                    )

                    spells.forEachIndexed { index, spell ->
                        key(spell.id, index) {
                            SpellItem(
                                spell = spell,
                                onClick = { editedSpellId = spell.id },
                                onRemove = { screenModel.removeSpell(spell) },
                                isLast = index == spells.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }

    editedSpellId?.let {
        EditSpellDialog(
            screenModel = screenModel,
            spellId = it,
            onDismissRequest = { editedSpellId = null },
        )
    }
}

@Composable
private fun SpellItem(
    spell: Spell,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    isLast: Boolean,
) {
    val strings = LocalStrings.current.spells

    CardItem(
        name = spell.name,
        onClick = onClick,
        showDivider = !isLast,
        contextMenuItems = listOf(ContextMenu.Item(LocalStrings.current.commonUi.buttonRemove, onRemove)),
        badge = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.tiny),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Text(strings.castingNumberShortcut)
                    Text(
                        spell.effectiveCastingNumber.toString(),
                        Modifier.padding(start = Spacing.tiny),
                    )
                }

                if (spell.memorized) {
                    Icon(
                        drawableResource(Resources.Drawable.MemorizeSpell),
                        strings.labelMemorized,
                        Modifier.size(16.dp),
                    )
                }
            }
        }
    )
}
