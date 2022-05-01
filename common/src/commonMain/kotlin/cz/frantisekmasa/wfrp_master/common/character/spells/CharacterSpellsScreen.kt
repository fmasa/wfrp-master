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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.AddSpellDialog
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.EditSpellDialog
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
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
    val spells = screenModel.spells.collectWithLifecycle(null).value ?: return

    if (spells.isEmpty()) {
        val messages = LocalStrings.current.spells.messages

        EmptyUI(
            text = messages.characterHasNoSpell,
            subText = messages.characterHasNoSpellSubtext,
            icon = Resources.Drawable.Spell,
        )
        return
    }

    var editedSpellId: Uuid? by rememberSaveable { mutableStateOf(null) }

    LazyColumn(contentPadding = PaddingValues(bottom = Spacing.bottomPaddingUnderFab)) {
        items(spells) { spell ->
            SpellItem(
                spell,
                onClick = { editedSpellId = spell.id },
                onRemove = { screenModel.removeSpell(spell) }
            )
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
private fun SpellItem(spell: Spell, onClick: () -> Unit, onRemove: () -> Unit) {
    val strings = LocalStrings.current.spells

    CardItem(
        name = spell.name,
        description = spell.effect,
        icon = { ItemIcon(Resources.Drawable.Spell, ItemIcon.Size.Small) },
        onClick = onClick,
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
