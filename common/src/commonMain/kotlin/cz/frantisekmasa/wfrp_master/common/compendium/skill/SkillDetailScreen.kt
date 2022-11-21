package cz.frantisekmasa.wfrp_master.common.compendium.skill

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.SkillDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId

class SkillDetailScreen(
    private val partyId: PartyId,
    private val skillId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        CompendiumItemDetailScreen(
            partyId = partyId,
            item = { it.getSkill(skillId) },
            detail = {
                SkillDetailBody(
                    characteristic = it.characteristic,
                    advanced = it.advanced,
                    description = it.description,
                )
            }
        ) { item, screenModel, onDismissRequest ->
            SkillDialog(
                skill = item,
                onDismissRequest = onDismissRequest,
                screenModel = screenModel,
            )
        }
    }
}
