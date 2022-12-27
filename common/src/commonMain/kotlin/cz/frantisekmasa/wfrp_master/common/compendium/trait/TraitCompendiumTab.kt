package cz.frantisekmasa.wfrp_master.common.compendium.trait

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
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumTab
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TraitCompendiumTab(
    partyId: PartyId,
    screenModel: TraitCompendiumScreenModel,
    width: Dp,
) {
    var newTraitDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (newTraitDialogOpened) {
        TraitDialog(
            trait = null,
            screenModel = screenModel,
            onDismissRequest = { newTraitDialogOpened = false },
        )
    }

    val navigator = LocalNavigator.currentOrThrow

    CompendiumTab(
        liveItems = screenModel.items,
        emptyUI = {
            val messages = LocalStrings.current.traits.messages
            EmptyUI(
                text = messages.noTraitsInCompendium,
                subText = messages.noTraitsInCompendiumSubtext,
                icon = Resources.Drawable.Trait,
            )
        },
        remover = screenModel::remove,
        newItemSaver = screenModel::createNew,
        onClick = { navigator.push(TraitDetailScreen(partyId, it.id)) },
        onNewItemRequest = { newTraitDialogOpened = true },
        width = width,
    ) { trait ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Trait) },
            text = { Text(trait.name) }
        )
        Divider()
    }
}
