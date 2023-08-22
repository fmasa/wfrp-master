package cz.frantisekmasa.wfrp_master.common.compendium.trapping

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.trappingIcon
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class TrappingCompendiumScreen(
    private val partyId: PartyId
) : CompendiumScreen() {

    @Composable
    override fun Content() {
        val screenModel: TrappingCompendiumScreenModel = rememberScreenModel(arg = partyId)
        var newTrappingDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current

        if (newTrappingDialogOpened) {
            TrappingDialog(
                existingTrapping = null,
                onDismissRequest = { newTrappingDialogOpened = false },
                onSaveRequest = {
                    screenModel.createNew(it)
                    navigation.navigate(CompendiumTrappingDetailScreen(partyId, it.id))
                }
            )
        }

        ItemsList(
            liveItems = screenModel.items,
            emptyUI = {
                EmptyUI(
                    text = stringResource(Str.trappings_messages_no_trappings_in_compendium),
                    subText = stringResource(Str.trappings_messages_no_trappings_in_compendium_subtext),
                    icon = Resources.Drawable.TrappingContainer,
                )
            },
            remover = screenModel::remove,
            newItemSaver = screenModel::createNew,
            onClick = { navigation.navigate(CompendiumTrappingDetailScreen(partyId, it.id)) },
            onNewItemRequest = { newTrappingDialogOpened = true },
            type = Type.SKILLS,
        ) { trapping ->
            ListItem(
                icon = { ItemIcon(trappingIcon(trapping.trappingType)) },
                text = { Text(trapping.name) },
                trailing = { VisibilityIcon(trapping) },
            )
            Divider()
        }
    }
}
