package cz.muni.fi.rpg.ui.character.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.ui.texts.SaveButtonText
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.ui.forms.*
import cz.muni.fi.rpg.ui.characterCreation.CharacterBasicInfoForm
import cz.muni.fi.rpg.ui.characterCreation.CharacterCharacteristicsForm
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

private object CharacterEditScreen {
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
                        character.getPoints().maxWounds.toString(),
                        Rules.PositiveInteger(),
                    ),
                    hardyTalent = rememberSaveable { mutableStateOf(character.hasHardyTalent()) }
                )
        }

        fun isValid() = maxWounds.value.toIntOrNull()?.let { it > 0 } ?: false
    }
}

@Composable
fun CharacterEditScreen(routing: Routing<Route.CharacterEdit>) {
    val viewModel: CharacterViewModel by viewModel { parametersOf(routing.route.characterId) }
    val coroutineScope = rememberCoroutineScope()

    val character = viewModel.character.observeAsState().value

    val submitEnabled = remember { mutableStateOf(true) }
    val formData = character?.let { CharacterEditScreen.FormData.fromCharacter(it) }
    val validate = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CharacterEditTopBar(
                character?.getName() ?: "",
                onSave = {
                    if (formData?.isValid() == true) {
                        submitEnabled.value = false

                        coroutineScope.launch(Dispatchers.IO) {
                            updateCharacter(viewModel, formData)
                            withContext(Dispatchers.Main) { routing.pop() }
                        }
                    } else {
                        validate.value = true
                    }
                },
                onBack = { routing.pop() },
                actionsEnabled = submitEnabled.value && character != null
            )
        }
    ) {
        formData?.let { CharacterEditMainUI(it, validate.value) }
    }
}

@Composable
private fun CharacterEditMainUI(formData: CharacterEditScreen.FormData, validate: Boolean) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(Modifier.padding(24.dp)) {
            CharacterBasicInfoForm(formData.basicInfo, validate)

            HorizontalLine()

            MaxWoundsSegment(formData.wounds, validate)

            HorizontalLine()

            Text(
                stringResource(R.string.title_character_characteristics),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 16.dp)
                    .fillMaxWidth()
            )

            CharacterCharacteristicsForm(formData.characteristics, validate)
        }
    }
}

@Composable
private fun MaxWoundsSegment(data: CharacterEditScreen.WoundsData, validate: Boolean) {
    Column(Modifier.padding(top = 20.dp)) {
        TextInput(
            label = stringResource(R.string.label_max_wounds),
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(bottom = 12.dp),
            value = data.maxWounds,
            maxLength = 3,
            keyboardType = KeyboardType.Number,
            validate = validate,
        )

        CheckboxWithText(
            text = stringResource(R.string.title_checkbox_hardy),
            checked = data.hardyTalent.value,
            onCheckedChange = { data.hardyTalent.value = it }
        )
    }
}

@Composable
private fun CharacterEditTopBar(
    title: String,
    onSave: () -> Unit,
    onBack: () -> Unit,
    actionsEnabled: Boolean,
) {
    TopAppBar(
        navigationIcon = { BackButton(onBack) },
        title = {
            Column {
                Text(title)
                Subtitle(stringResource(R.string.subtitle_edit_character))
            }
        },
        actions = {
            TopBarAction(onClick = onSave, enabled = actionsEnabled) { SaveButtonText() }
        }
    )
}

private suspend fun updateCharacter(
    viewModel: CharacterViewModel,
    formData: CharacterEditScreen.FormData
) {
    viewModel.update {
        it.update(
            name = formData.basicInfo.name.value,
            career = formData.basicInfo.career.value,
            race = formData.basicInfo.race.value,
            psychology = formData.basicInfo.psychology.value,
            motivation = formData.basicInfo.motivation.value,
            socialClass = formData.basicInfo.socialClass.value,
            note = formData.basicInfo.note.value,
            characteristicsBase = formData.characteristics.toBaseCharacteristics(),
            characteristicsAdvances = formData.characteristics.toCharacteristicAdvances(),
            maxWounds = formData.wounds.maxWounds.value.toInt(),
            hardyTalent = formData.wounds.hardyTalent.value,
        )
    }
}