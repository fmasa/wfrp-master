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
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumTab
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TalentCompendiumTab(partyId: PartyId, screenModel: CompendiumScreenModel, width: Dp) {
    var newTalentDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (newTalentDialogOpened) {
        TalentDialog(
            talent = null,
            screenModel = screenModel,
            onDismissRequest = { newTalentDialogOpened = false },
        )
    }

    val navigator = LocalNavigator.currentOrThrow

    CompendiumTab(
        liveItems = screenModel.talents,
        emptyUI = {
            val messages = LocalStrings.current.talents.messages
            EmptyUI(
                text = messages.noTalentsInCompendium,
                subText = messages.noTalentsInCompendiumSubtext,
                icon = Resources.Drawable.Talent,
            )
        },
        remover = screenModel::remove,
        saver = screenModel::save,
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
