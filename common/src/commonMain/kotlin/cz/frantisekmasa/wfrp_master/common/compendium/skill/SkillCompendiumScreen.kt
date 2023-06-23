package cz.frantisekmasa.wfrp_master.common.compendium.skill

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilityIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

class SkillCompendiumScreen(
    private val partyId: PartyId
) : CompendiumScreen() {

    @Composable
    override fun Content() {
        val screenModel: SkillCompendiumScreenModel = rememberScreenModel(arg = partyId)
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

        ItemsList(
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
            type = Type.SKILLS,
        ) { skill ->
            ListItem(
                icon = { ItemIcon(skill.characteristic.getIcon()) },
                text = { Text(skill.name) },
                trailing = { VisibilityIcon(skill) },
            )
            Divider()
        }
    }
}
