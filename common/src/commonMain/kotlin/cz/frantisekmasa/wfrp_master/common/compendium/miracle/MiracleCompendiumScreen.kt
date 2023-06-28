package cz.frantisekmasa.wfrp_master.common.compendium.miracle

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

class MiracleCompendiumScreen(
    private val partyId: PartyId
) : CompendiumScreen() {

    @Composable
    override fun Content() {
        val screenModel: MiracleCompendiumScreenModel = rememberScreenModel(arg = partyId)
        var newMiracleDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current

        if (newMiracleDialogOpened) {
            MiracleDialog(
                miracle = null,
                onDismissRequest = { newMiracleDialogOpened = false },
                onSaveRequest = {
                    screenModel.createNew(it)
                    navigation.navigate(MiracleDetailScreen(partyId, it.id))
                },
            )
        }

        ItemsList(
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
            onClick = { navigation.navigate(MiracleDetailScreen(partyId, it.id)) },
            onNewItemRequest = { newMiracleDialogOpened = true },
            type = Type.MIRACLES,
        ) { miracle ->
            ListItem(
                icon = { ItemIcon(Resources.Drawable.Miracle) },
                text = { Text(miracle.name) },
                trailing = { VisibilityIcon(miracle) },
            )
            Divider()
        }
    }
}
