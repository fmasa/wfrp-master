package cz.frantisekmasa.wfrp_master.common.compendium.disease

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

class DiseaseCompendiumScreen(
    private val partyId: PartyId,
) : CompendiumScreen() {
    @Composable
    override fun Content() {
        val screenModel: DiseaseCompendiumScreenModel = rememberScreenModel(arg = partyId)
        var newDiseaseDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current

        if (newDiseaseDialogOpened) {
            DiseaseDialog(
                disease = null,
                onDismissRequest = { newDiseaseDialogOpened = false },
                onSaveRequest = {
                    screenModel.createNew(it)
                    navigation.navigate(CompendiumDiseaseDetailScreen(partyId, it.id))
                },
            )
        }

        ItemsList(
            liveItems = screenModel.items,
            emptyUI = {
                EmptyUI(
                    text = stringResource(Str.diseases_messages_no_diseases_in_compendium),
                    subText = stringResource(Str.diseases_messages_no_diseases_in_compendium_subtext),
                    icon = Resources.Drawable.Disease,
                )
            },
            remover = screenModel::remove,
            newItemSaver = screenModel::createNew,
            onClick = { navigation.navigate(CompendiumDiseaseDetailScreen(partyId, it.id)) },
            onNewItemRequest = { newDiseaseDialogOpened = true },
            type = Type.TRAPPINGS,
        ) { disease ->
            ListItem(
                icon = { ItemIcon(Resources.Drawable.Disease) },
                text = { Text(disease.name) },
                trailing = { VisibilityIcon(disease) },
            )
            Divider()
        }
    }
}
