package cz.frantisekmasa.wfrp_master.common.compendium.spell

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
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class SpellCompendiumScreen(
    private val partyId: PartyId
) : CompendiumScreen() {

    @Composable
    override fun Content() {
        val screenModel: SpellCompendiumScreenModel = rememberScreenModel(arg = partyId)
        var newSpellDialogOpened by rememberSaveable { mutableStateOf(false) }
        val navigation = LocalNavigationTransaction.current

        if (newSpellDialogOpened) {
            SpellDialog(
                spell = null,
                onDismissRequest = { newSpellDialogOpened = false },
                onSaveRequest = {
                    screenModel.createNew(it)
                    navigation.navigate(CompendiumSpellDetailScreen(partyId, it.id))
                },
            )
        }

        ItemsList(
            liveItems = screenModel.items,
            emptyUI = {
                EmptyUI(
                    text = stringResource(Str.spells_messages_no_spells_in_compendium),
                    subText = stringResource(Str.spells_messages_no_spells_in_compendium_subtext),
                    icon = Resources.Drawable.Spell
                )
            },
            remover = screenModel::remove,
            newItemSaver = screenModel::createNew,
            onClick = { navigation.navigate(CompendiumSpellDetailScreen(partyId, it.id)) },
            onNewItemRequest = { newSpellDialogOpened = true },
            type = Type.SPELLS,
        ) { spell ->
            ListItem(
                icon = { SpellLoreIcon(spell.lore) },
                text = { Text(spell.name) },
                trailing = { VisibilityIcon(spell) },
            )
            Divider()
        }
    }
}
