package cz.frantisekmasa.wfrp_master.common.compendium.career

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class CareerCompendiumScreen(
    private val partyId: PartyId,
) : CompendiumScreen() {
    @Composable
    override fun Content() {
        val screenModel: CareerCompendiumScreenModel = rememberScreenModel(arg = partyId)
        val navigation = LocalNavigationTransaction.current

        var newCareerDialogOpened by remember { mutableStateOf(false) }

        if (newCareerDialogOpened) {
            CareerFormDialog(
                title = stringResource(Str.careers_title_new_career),
                existingCareer = null,
                onSaveRequest = {
                    val id = uuid4()
                    screenModel.createNew(
                        Career(
                            id = id,
                            name = it.name,
                            description = it.description,
                            socialClass = it.socialClass,
                            races = it.races,
                            levels = emptyList(),
                        ),
                    )

                    navigation.navigate(CompendiumCareerDetailScreen(partyId, id))
                },
                onDismissRequest = { newCareerDialogOpened = false },
            )
        }

        ItemsList(
            liveItems = screenModel.careers,
            emptyUI = {
                EmptyUI(
                    text = stringResource(Str.careers_messages_no_careers_in_compendium),
                    subText = stringResource(Str.careers_messages_no_careers_in_compendium_subtext),
                    icon = Resources.Drawable.Career,
                )
            },
            remover = screenModel::remove,
            newItemSaver = screenModel::createNew,
            onNewItemRequest = { newCareerDialogOpened = true },
            onClick = { navigation.navigate(CompendiumCareerDetailScreen(partyId, it.id)) },
            type = Type.CAREERS,
        ) { career ->
            ListItem(
                icon = { ItemIcon(Resources.Drawable.Career) },
                text = { Text(career.name) },
                trailing = { VisibilityIcon(career) },
            )
            Divider()
        }
    }
}
