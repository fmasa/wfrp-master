package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.DiseaseSpecification
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases.DiseaseSpecificationForm
import cz.frantisekmasa.wfrp_master.common.compendium.disease.CompendiumDiseaseDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Countdown
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterDiseaseDetailScreen(characterId: CharacterId, private val diseaseId: Uuid) :
    CharacterItemDetailScreen(characterId, diseaseId) {
    @Composable
    override fun Content() {
        val screenModel: CharacterDiseaseDetailScreenModel = rememberScreenModel(arg = characterId)

        Detail(
            isGameMasterFlow = screenModel.isGameMaster,
            itemFlow = remember { screenModel.getDiseaseDetail(diseaseId) },
        ) { state, isGameMaster ->
            val navigation = LocalNavigationTransaction.current
            val disease = state.disease

            if (disease.compendiumId != null) {
                val coroutineScope = rememberCoroutineScope()

                var edit by rememberSaveable { mutableStateOf(false) }

                if (edit) {
                    DiseaseSpecificationForm(
                        existingDisease =
                            DiseaseSpecification.Data(
                                incubation = disease.incubation,
                                duration = disease.duration,
                                isDiagnosed = disease.isDiagnosed,
                            ),
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
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                navigationIcon = { CloseButton(navigation::goBack) },
                                title = { Text(disease.name) },
                                actions = {
                                    IconAction(
                                        Icons.Rounded.Edit,
                                        stringResource(Str.character_title_edit),
                                        onClick = { edit = true },
                                    )
                                },
                            )
                        },
                    ) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
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
                            DiseaseDetailBody(
                                subheadBar = {
                                    if (isGameMaster) {
                                        SingleLineTextValue(
                                            label = stringResource(Str.diseases_label_diagnosed),
                                            value =
                                                stringResource(
                                                    if (disease.isDiagnosed) {
                                                        Str.common_ui_boolean_yes
                                                    } else {
                                                        Str.common_ui_boolean_no
                                                    },
                                                ),
                                        )
                                    }
                                },
                                symptoms = state.symptoms,
                                permanentEffects = disease.permanentEffects,
                                description = disease.description,
                            )
                        }
                    }
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

data class CharacterDiseaseDetailScreenState(
    val disease: Disease,
    val symptoms: ImmutableList<Symptom>,
)
