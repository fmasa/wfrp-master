package cz.frantisekmasa.wfrp_master.common.compendium.skill

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
fun SkillCompendiumTab(partyId: PartyId, screenModel: SkillCompendiumScreenModel, width: Dp) {
    var newSkillDialogOpened by rememberSaveable { mutableStateOf(false) }
    val navigator = LocalNavigator.currentOrThrow

    if (newSkillDialogOpened) {
        SkillDialog(
            skill = null,
            onDismissRequest = { newSkillDialogOpened = false },
            onSaveRequest = {
                screenModel.createNew(it)
                navigator.push(SkillDetailScreen(partyId, it.id))
            }
        )
    }

    CompendiumTab(
        liveItems = screenModel.items,
        emptyUI = {
            val messages = LocalStrings.current.skills.messages
            EmptyUI(
                text = messages.noSkillsInCompendium,
                subText = messages.noSkillsInCompendiumSubtext,
                icon = Resources.Drawable.Skill,
            )
        },
        remover = screenModel::remove,
        newItemSaver = screenModel::createNew,
        onClick = { navigator.push(SkillDetailScreen(partyId, it.id)) },
        onNewItemRequest = { newSkillDialogOpened = true },
        width = width,
    ) { skill ->
        ListItem(
            icon = { ItemIcon(skill.characteristic.getIcon()) },
            text = { Text(skill.name) },
            trailing = { VisibilityIcon(skill) },
        )
        Divider()
    }
}
