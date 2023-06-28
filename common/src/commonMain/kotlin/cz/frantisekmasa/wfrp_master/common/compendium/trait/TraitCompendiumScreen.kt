package cz.frantisekmasa.wfrp_master.common.compendium.trait

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

class TraitCompendiumScreen(
    private val partyId: PartyId,
) : CompendiumScreen() {

    @Composable
    override fun Content() {
        val screenModel: TraitCompendiumScreenModel = rememberScreenModel(arg = partyId)
        var newTraitDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current

        if (newTraitDialogOpened) {
            TraitDialog(
                trait = null,
                onDismissRequest = { newTraitDialogOpened = false },
                onSaveRequest = {
                    screenModel.createNew(it)
                    navigation.navigate(TraitDetailScreen(partyId, it.id))
                }
            )
        }

        ItemsList(
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
            onClick = { navigation.navigate(TraitDetailScreen(partyId, it.id)) },
            onNewItemRequest = { newTraitDialogOpened = true },
            type = Type.TRAITS,
        ) { trait ->
            ListItem(
                icon = { ItemIcon(Resources.Drawable.Trait) },
                text = { Text(trait.name) },
                trailing = { VisibilityIcon(trait) },
            )
            Divider()
        }
    }
}
