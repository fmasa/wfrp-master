package cz.frantisekmasa.wfrp_master.common.compendium.spell

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumTab
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SpellCompendiumTab(partyId: PartyId, screenModel: CompendiumScreenModel, width: Dp) {
    var newSpellDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (newSpellDialogOpened) {
        SpellDialog(
            spell = null,
            screenModel = screenModel,
            onDismissRequest = { newSpellDialogOpened = false },
        )
    }

    val navigator = LocalNavigator.currentOrThrow

    CompendiumTab(
        liveItems = screenModel.spells,
        emptyUI = {
            val messages = LocalStrings.current.spells.messages
            EmptyUI(
                text = messages.noSpellsInCompendium,
                subText = messages.noSpellsInCompendiumSubtext,
                icon = Resources.Drawable.Spell
            )
        },
        remover = screenModel::remove,
        saver = screenModel::save,
        onClick = { navigator.push(SpellDetailScreen(partyId, it.id)) },
        onNewItemRequest = { newSpellDialogOpened = true },
        width = width,
    ) { spell ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Spell) },
            text = { Text(spell.name) }
        )
        Divider()
    }
}
