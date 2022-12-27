package cz.frantisekmasa.wfrp_master.common.compendium.miracle

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
fun MiracleCompendiumTab(partyId: PartyId, screenModel: MiracleCompendiumScreenModel, width: Dp) {
    var newMiracleDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (newMiracleDialogOpened) {
        MiracleDialog(
            miracle = null,
            screenModel = screenModel,
            onDismissRequest = { newMiracleDialogOpened = false },
        )
    }

    val navigator = LocalNavigator.currentOrThrow

    CompendiumTab(
        liveItems = screenModel.items,
        emptyUI = {
            val strings = LocalStrings.current.miracles

            EmptyUI(
                text = strings.messages.noMiraclesInCompendium,
                subText = strings.messages.noMiraclesInCompendiumSubtext,
                icon = Resources.Drawable.Miracle
            )
        },
        remover = screenModel::remove,
        newItemSaver = screenModel::createNew,
        onClick = { navigator.push(MiracleDetailScreen(partyId, it.id)) },
        onNewItemRequest = { newMiracleDialogOpened = true },
        width = width,
    ) { miracle ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Miracle) },
            text = { Text(miracle.name) }
        )
        Divider()
    }
}
