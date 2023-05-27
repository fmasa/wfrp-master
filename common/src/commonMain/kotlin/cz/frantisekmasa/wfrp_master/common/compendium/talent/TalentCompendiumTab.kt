package cz.frantisekmasa.wfrp_master.common.compendium.talent

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
fun TalentCompendiumTab(partyId: PartyId, screenModel: TalentCompendiumScreenModel, width: Dp) {
    var newTalentDialogOpened by rememberSaveable { mutableStateOf(false) }
    val navigator = LocalNavigator.currentOrThrow

    if (newTalentDialogOpened) {
        TalentDialog(
            talent = null,
            onDismissRequest = { newTalentDialogOpened = false },
            onSaveRequest = {
                screenModel.createNew(it)
                navigator.push(TalentDetailScreen(partyId, it.id))
            }
        )
    }

    CompendiumTab(
        liveItems = screenModel.items,
        emptyUI = {
            val messages = LocalStrings.current.talents.messages
            EmptyUI(
                text = messages.noTalentsInCompendium,
                subText = messages.noTalentsInCompendiumSubtext,
                icon = Resources.Drawable.Talent,
            )
        },
        remover = screenModel::remove,
        newItemSaver = screenModel::createNew,
        onClick = { navigator.push(TalentDetailScreen(partyId, it.id)) },
        onNewItemRequest = { newTalentDialogOpened = true },
        width = width,
    ) { talent ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Talent) },
            text = { Text(talent.name) }
        )
        Divider()
    }
}
