package cz.frantisekmasa.wfrp_master.common.character.spells

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.NonCompendiumSpellForm
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.SpellDetail
import cz.frantisekmasa.wfrp_master.common.compendium.spell.CompendiumSpellDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterSpellDetailScreen(characterId: CharacterId, spellId: Uuid) :
    CharacterItemDetailScreen(characterId, spellId) {

    @Composable
    override fun Content() {
        val screenModel: CharacterSpellDetailScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { spell, isGameMaster ->
            val navigation = LocalNavigationTransaction.current

            if (spell.compendiumId != null) {
                val coroutineScope = rememberCoroutineScope()

                SpellDetail(
                    spell = spell,
                    onDismissRequest = navigation::goBack,
                    subheadBar = {
                        SubheadBar {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(stringResource(Str.spells_label_memorized))
                                Switch(
                                    checked = spell.memorized,
                                    onCheckedChange = { memorized ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            screenModel.saveSpell(spell.copy(memorized = memorized))
                                        }
                                    },
                                )
                            }
                        }

                        if (isGameMaster) {
                            CompendiumButton(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = Spacing.bodyPadding),
                                onClick = {
                                    navigation.navigate(
                                        CompendiumSpellDetailScreen(
                                            characterId.partyId,
                                            spell.compendiumId,
                                        )
                                    )
                                }
                            )
                        }
                    },
                )
            } else {
                NonCompendiumSpellForm(
                    onSave = screenModel::saveSpell,
                    existingSpell = spell,
                    onDismissRequest = navigation::goBack,
                )
            }
        }
    }
}
