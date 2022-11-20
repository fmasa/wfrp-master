package cz.frantisekmasa.wfrp_master.common.compendium.spell

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.SpellDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId

class SpellDetailScreen(
    private val partyId: PartyId,
    private val spellId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        CompendiumItemDetailScreen(
            partyId = partyId,
            item = { it.getSpell(spellId) },
            detail = {
                SpellDetailBody(
                    castingNumber = it.castingNumber,
                    effectiveCastingNumber = it.castingNumber,
                    range = it.range,
                    target = it.target,
                    duration = it.duration,
                    effect = it.effect,
                )
            }
        ) { item, screenModel, onDismissRequest ->
            SpellDialog(
                spell = item,
                onDismissRequest = onDismissRequest,
                screenModel = screenModel,
            )
        }
    }
}
