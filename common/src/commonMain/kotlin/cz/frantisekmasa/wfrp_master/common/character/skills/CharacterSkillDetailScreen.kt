package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.NonCompendiumSkillForm
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.SkillDetail
import cz.frantisekmasa.wfrp_master.common.compendium.skill.CompendiumSkillDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterSkillDetailScreen(characterId: CharacterId, skillId: Uuid) :
    CharacterItemDetailScreen(characterId, skillId) {

    @Composable
    override fun Content() {
        val screenModel: CharacterSkillDetailScreenModel = rememberScreenModel(arg = characterId)
        val characterScreenModel: CharacterScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { skill, isGameMaster ->
            val navigation = LocalNavigationTransaction.current
            val characteristics = characterScreenModel.character
                .collectWithLifecycle(null).value?.characteristics

            if (characteristics == null) {
                FullScreenProgress()
                return@Detail
            }

            if (skill.compendiumId != null) {
                val coroutineScope = rememberCoroutineScope()

                SkillDetail(
                    skill,
                    onDismissRequest = navigation::goBack,
                    subheadBar = {
                        AdvancesBar(
                            advances = skill.advances,
                            minAdvances = if (skill.advanced) 1 else 0,
                            onAdvancesChange = { advances ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    screenModel.saveSkill(skill.copy(advances = advances))
                                }
                            },
                        )

                        SkillRating(
                            label = stringResource(Str.skills_label_rating),
                            value = characteristics.get(skill.characteristic) + skill.advances,
                            modifier = Modifier
                                .padding(top = Spacing.extraLarge)
                                .align(Alignment.CenterHorizontally),
                        )

                        if (isGameMaster) {
                            CompendiumButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    navigation.navigate(
                                        CompendiumSkillDetailScreen(
                                            screenModel.characterId.partyId,
                                            skill.compendiumId,
                                        )
                                    )
                                }
                            )
                        }
                    }
                )
            } else {
                NonCompendiumSkillForm(
                    onSave = screenModel::saveSkill,
                    existingSkill = skill,
                    characteristics = characteristics,
                    onDismissRequest = navigation::goBack,
                )
            }
        }
    }
}

@Composable
private fun AdvancesBar(
    advances: Int,
    minAdvances: Int,
    onAdvancesChange: (advances: Int) -> Unit
) {
    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Str.skills_label_advances))
            NumberPicker(
                value = advances,
                onIncrement = { onAdvancesChange(advances + 1) },
                onDecrement = {
                    if (advances > minAdvances) {
                        onAdvancesChange(advances - 1)
                    }
                }
            )
        }
    }
}
