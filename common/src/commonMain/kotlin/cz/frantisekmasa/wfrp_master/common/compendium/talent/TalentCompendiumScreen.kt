package cz.frantisekmasa.wfrp_master.common.compendium.talent

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class TalentCompendiumScreen(
    private val partyId: PartyId,
) : CompendiumScreen() {
    @Composable
    override fun Content() {
        val screenModel: TalentCompendiumScreenModel = rememberScreenModel(arg = partyId)
        var newTalentDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current

        if (newTalentDialogOpened) {
            TalentDialog(
                talent = null,
                onDismissRequest = { newTalentDialogOpened = false },
                onSaveRequest = {
                    screenModel.createNew(it)
                    navigation.navigate(CompendiumTalentDetailScreen(partyId, it.id))
                },
            )
        }

        ItemsList(
            liveItems = screenModel.items,
            emptyUI = {
                EmptyUI(
                    text = stringResource(Str.talents_messages_no_talents_in_compendium),
                    subText = stringResource(Str.talents_messages_no_talents_in_compendium_subtext),
                    icon = Resources.Drawable.Talent,
                )
            },
            remover = screenModel::remove,
            newItemSaver = screenModel::createNew,
            onClick = { navigation.navigate(CompendiumTalentDetailScreen(partyId, it.id)) },
            onNewItemRequest = { newTalentDialogOpened = true },
            type = Type.TALENTS,
        ) { talent ->
            ListItem(
                icon = { ItemIcon(Resources.Drawable.Talent) },
                text = { Text(talent.name) },
                trailing = { VisibilityIcon(talent) },
            )
            Divider()
        }
    }
}
