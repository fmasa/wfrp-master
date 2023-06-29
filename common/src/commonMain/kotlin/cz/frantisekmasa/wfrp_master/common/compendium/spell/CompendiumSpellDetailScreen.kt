package cz.frantisekmasa.wfrp_master.common.compendium.spell

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.SpellDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class CompendiumSpellDetailScreen(
    private val partyId: PartyId,
    private val spellId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: SpellCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = spellId,
            screenModel = screenModel,
            detail = {
                SpellDetailBody(
                    castingNumber = it.castingNumber,
                    effectiveCastingNumber = it.castingNumber,
                    range = it.range,
                    target = it.target,
                    lore = it.lore,
                    duration = it.duration,
                    effect = it.effect,
                )
            }
        ) { item, onDismissRequest ->
            SpellDialog(
                spell = item,
                onDismissRequest = onDismissRequest,
                onSaveRequest = screenModel::update,
            )
        }
    }
}
