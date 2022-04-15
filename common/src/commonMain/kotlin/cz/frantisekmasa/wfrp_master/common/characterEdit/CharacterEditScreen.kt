package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterBasicInfoForm
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCharacteristicsForm
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CharacterEditScreen(
    private val characterId: CharacterId
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: CharacterScreenModel = rememberScreenModel(arg = characterId)
        val coroutineScope = rememberCoroutineScope()

        val character = screenModel.character.collectWithLifecycle(null).value

        if (character == null) {
            FullScreenProgress()
            return
        }

        val submitEnabled = remember { mutableStateOf(true) }
        val formData = character.let { CharacterEditScreen.FormData.fromCharacter(it) }
        val validate = remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                val navigator = LocalNavigator.currentOrThrow

                CharacterEditTopBar(
                    character.name,
                    onSave = {
                        if (formData.isValid()) {
                            submitEnabled.value = false

                            coroutineScope.launch(Dispatchers.IO) {
                                updateCharacter(screenModel, formData)
                                navigator.pop()
                            }
                        } else {
                            validate.value = true
                        }
                    },
                    actionsEnabled = submitEnabled.value
                )
            },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding)
            ) {
                EditableCharacterAvatar(
                    screenModel = screenModel,
                    character = character,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                CharacterEditMainUI(formData, validate.value)
            }
        }
    }

    @Stable
    class FormData(
        val basicInfo: CharacterBasicInfoForm.Data,
        val characteristics: CharacterCharacteristicsForm.Data,
        val wounds: WoundsData,
    ) {
        companion object {
            @Composable
            fun fromCharacter(character: Character) = FormData(
                basicInfo = CharacterBasicInfoForm.Data.fromCharacter(character),
                characteristics = CharacterCharacteristicsForm.Data.fromCharacter(character),
                wounds = WoundsData.fromCharacter(character),
            )
        }

        fun isValid() = basicInfo.isValid() && characteristics.isValid() && wounds.isValid()
    }

    @Stable
    class WoundsData(
        val maxWounds: InputValue,
        val hardyTalent: MutableState<Boolean>,
    ) {
        companion object {
            @Composable
            fun fromCharacter(character: Character) =
                WoundsData(
                    maxWounds = inputValue(
                        character.points.maxWounds.toString(),
                        Rules.PositiveInteger(),
                    ),
                    hardyTalent = rememberSaveable { mutableStateOf(character.hasHardyTalent) }
                )
        }

        fun isValid() = maxWounds.value.toIntOrNull()?.let { it > 0 } ?: false
    }
}


@Composable
private fun CharacterEditMainUI(
    formData: CharacterEditScreen.FormData,
    validate: Boolean
) {
    CharacterBasicInfoForm(formData.basicInfo, validate)

    HorizontalLine()

    MaxWoundsSegment(formData.wounds, validate)

    HorizontalLine()

    Text(
        LocalStrings.current.character.titleCharacteristics,
        style = MaterialTheme.typography.h6,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 20.dp, bottom = 16.dp)
            .fillMaxWidth()
    )

    CharacterCharacteristicsForm(formData.characteristics, validate)
}

@Composable
private fun MaxWoundsSegment(data: CharacterEditScreen.WoundsData, validate: Boolean) {
    val strings = LocalStrings.current.points

    Column(Modifier.padding(top = 20.dp)) {
        TextInput(
            label = strings.maxWounds,
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(bottom = 12.dp),
            value = data.maxWounds,
            maxLength = 3,
            keyboardType = KeyboardType.Number,
            validate = validate,
        )

        CheckboxWithText(
            text = strings.labelHardy,
            checked = data.hardyTalent.value,
            onCheckedChange = { data.hardyTalent.value = it }
        )
    }
}

@Composable
private fun CharacterEditTopBar(
    title: String,
    onSave: () -> Unit,
    actionsEnabled: Boolean,
) {
    TopAppBar(
        navigationIcon = { BackButton() },
        title = {
            Column {
                Text(title)
                Subtitle(LocalStrings.current.character.titleEdit)
            }
        },
        actions = {
            SaveAction(enabled = actionsEnabled, onClick = onSave)
        }
    )
}

private suspend fun updateCharacter(
    screenModel: CharacterScreenModel,
    formData: CharacterEditScreen.FormData
) {
    val characteristics = formData.characteristics.toValue()

    screenModel.update {
        it.update(
            name = formData.basicInfo.name.value,
            career = formData.basicInfo.career.value,
            race = formData.basicInfo.race.value,
            psychology = formData.basicInfo.psychology.value,
            motivation = formData.basicInfo.motivation.value,
            socialClass = formData.basicInfo.socialClass.value,
            status = SocialStatus(
                formData.basicInfo.socialTier.value,
                formData.basicInfo.socialStanding.value,
            ),
            note = formData.basicInfo.note.value,
            characteristicsBase = characteristics.base,
            characteristicsAdvances = characteristics.advances,
            maxWounds = formData.wounds.maxWounds.value.toInt(),
            hasHardyTalent = formData.wounds.hardyTalent.value,
        )
    }
}
