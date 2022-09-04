package cz.frantisekmasa.wfrp_master.common.compendium.career

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CareerCompendiumTab(partyId: PartyId, screenModel: CompendiumScreenModel, width: Dp) {
    val strings = LocalStrings.current.careers.messages
    val navigator = LocalNavigator.currentOrThrow

    var newCareerDialogOpened by remember { mutableStateOf(false) }

    if (newCareerDialogOpened) {
        CareerFormDialog(
            title = LocalStrings.current.careers.titleNewCareer,
            existingCareer = null,
            onSave = {
                val id = uuid4()
                screenModel.save(
                    Career(
                        id = id,
                        name = it.name,
                        description = it.description,
                        socialClass = it.socialClass,
                        races = it.races,
                        levels = emptyList(),
                    )
                )

                navigator.push(CareerDetailScreen(partyId, id))
            },
            onDismissRequest = { newCareerDialogOpened = false },
        )
    }

    CompendiumTab(
        liveItems = screenModel.careers,
        emptyUI = {
            EmptyUI(
                text = strings.noCareersInCompendium,
                subText = strings.noCareersInCompendiumSubtext,
                icon = Resources.Drawable.Career,
            )
        },
        remover = screenModel::remove,
        saver = screenModel::save,
        onNewItemRequest = { newCareerDialogOpened = true },
        onClick = { navigator.push(CareerDetailScreen(partyId, it.id)) },
        width = width,
    ) { career ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Career) },
            text = { Text(career.name) }
        )
        Divider()
    }
}
