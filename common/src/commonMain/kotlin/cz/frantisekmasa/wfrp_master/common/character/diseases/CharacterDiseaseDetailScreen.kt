package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.DiseaseSpecificationForm
import cz.frantisekmasa.wfrp_master.common.compendium.disease.CompendiumDiseaseDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Countdown
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterDiseaseDetailScreen(characterId: CharacterId, diseaseId: Uuid) :
    CharacterItemDetailScreen(characterId, diseaseId) {
    @Composable
    override fun Content() {
        val screenModel: CharacterDiseaseDetailScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { disease, isGameMaster ->
            val navigation = LocalNavigationTransaction.current

            if (disease.compendiumId != null) {
                val coroutineScope = rememberCoroutineScope()

                var edit by rememberSaveable { mutableStateOf(false) }

                if (edit) {
                    DiseaseSpecificationForm(
                        existingDisease = disease,
                        onSave = {
                            screenModel.saveDisease(
                                disease =
                                    disease.copy(
                                        incubation = it.incubation,
                                        duration = it.duration,
                                        isDiagnosed = it.isDiagnosed,
                                    ),
                            )
                        },
                        onDismissRequest = { edit = false },
                        isGameMaster = isGameMaster,
                        compendiumDisease = null,
                    )
                } else {
                    DiseaseDetail(
                        subheadBar = {
                            when {
                                disease.incubation.value != 0 -> {
                                    CountdownBar(
                                        stringResource(Str.diseases_label_incubation),
                                        disease.incubation,
                                    ) { incubation ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            screenModel.saveDisease(disease.copy(incubation = incubation))
                                        }
                                    }
                                }

                                disease.duration.value != 0 -> {
                                    CountdownBar(
                                        stringResource(Str.diseases_label_duration),
                                        disease.duration,
                                    ) { duration ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            screenModel.saveDisease(disease.copy(duration = duration))
                                        }
                                    }
                                }

                                disease.isHealed -> {
                                    SubheadBar {
                                        Text(
                                            text = stringResource(Str.diseases_label_healed),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }

                            if (isGameMaster) {
                                CompendiumButton(
                                    modifier =
                                        Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = Spacing.bodyPadding),
                                    onClick = {
                                        navigation.navigate(
                                            CompendiumDiseaseDetailScreen(
                                                screenModel.characterId.partyId,
                                                disease.compendiumId,
                                            ),
                                        )
                                    },
                                )
                            }
                        },
                        disease = disease,
                        onDismissRequest = navigation::goBack,
                        onEditRequest = { edit = true },
                        isGameMaster = isGameMaster,
                    )
                }
            } else {
                NonCompendiumDiseaseForm(
                    onSave = screenModel::saveDisease,
                    existingDisease = disease,
                    onDismissRequest = navigation::goBack,
                    isGameMaster = isGameMaster,
                )
            }
        }
    }
}

@Composable
private fun CountdownBar(
    text: String,
    countdown: Countdown,
    onValueChange: (value: Countdown) -> Unit,
) {
    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val value = countdown.value
            Text("$text (${countdown.unit.localizedName})")
            NumberPicker(
                value = value,
                onIncrement = { onValueChange(countdown.copy(value = value + 1)) },
                onDecrement = {
                    if (value > 0) {
                        onValueChange(countdown.copy(value = value - 1))
                    }
                },
            )
        }
    }
}
