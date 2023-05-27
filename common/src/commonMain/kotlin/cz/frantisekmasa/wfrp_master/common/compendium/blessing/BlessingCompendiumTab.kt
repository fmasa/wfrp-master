package cz.frantisekmasa.wfrp_master.common.compendium.blessing

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
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun BlessingCompendiumTab(partyId: PartyId, screenModel: BlessingCompendiumScreenModel, width: Dp) {
    val messages = LocalStrings.current.blessings.messages
    var newBlessingDialogOpened by rememberSaveable { mutableStateOf(false) }
    val navigator = LocalNavigator.currentOrThrow

    if (newBlessingDialogOpened) {
        BlessingDialog(
            blessing = null,
            onDismissRequest = { newBlessingDialogOpened = false },
            onSaveRequest = {
                screenModel.createNew(it)
                navigator.push(BlessingDetailScreen(partyId, it.id))
            },
        )
    }

    CompendiumTab(
        liveItems = screenModel.items,
        emptyUI = {
            EmptyUI(
                text = messages.noBlessingsInCompendium,
                subText = messages.noBlessingsInCompendiumSubtext,
                icon = Resources.Drawable.Blessing
            )
        },
        remover = screenModel::remove,
        newItemSaver = screenModel::createNew,
        onNewItemRequest = { newBlessingDialogOpened = true },
        onClick = { navigator.push(BlessingDetailScreen(partyId, it.id)) },
        width = width,
    ) { blessing ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Blessing) },
            text = { Text(blessing.name) },
            trailing = { VisibilityIcon(blessing) },
        )
        Divider()
    }
}
