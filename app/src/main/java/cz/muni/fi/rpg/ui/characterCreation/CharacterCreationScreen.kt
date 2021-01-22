package cz.muni.fi.rpg.ui.characterCreation

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.CharacterCreationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

private enum class FormState {
    EDITED_BY_USER,
    CREATING_CHARACTER,
}

@Composable
fun CharacterCreationScreen(routing: Routing<Route.CharacterCreation>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_characterCreation))
                },
                navigationIcon = { BackButton(onClick = { routing.pop() }) }
            )
        }
    ) {
        MainContainer(routing)
    }
}

@Composable
private fun MainContainer(routing: Routing<Route.CharacterCreation>) {
    val viewModel: CharacterCreationViewModel by viewModel { parametersOf(routing.route.partyId) }
    val coroutineScope = rememberCoroutineScope()

    val validate = remember { mutableStateOf(false) }
    val currentStepIndex = savedInstanceState { 0 }
    val formState = savedInstanceState { FormState.EDITED_BY_USER }

    val basicInfo = CharacterBasicInfoForm.Data.empty()
    val characteristics = CharacterCharacteristicsForm.Data.fromCharacter(null)
    val points = PointsPoolForm.Data.empty()


    val saveCharacter = {
        formState.value = FormState.CREATING_CHARACTER
        coroutineScope.launch(Dispatchers.IO) {
            val characterId = viewModel.createCharacter(
                routing.route.userId,
                basicInfo,
                characteristics,
                points
            )

            withContext(Dispatchers.Main) {
                routing.navigateTo(Route.CharacterDetail(characterId), popUpTo = Route.PartyList)
            }
        }
    }

    val steps = listOf(
        WizardStep(R.string.title_character_creation_info, basicInfo) {
            CharacterBasicInfoForm(it, validate = validate.value)
        },
        WizardStep(R.string.title_character_stats, characteristics) {
            CharacterCharacteristicsForm(it, validate = validate.value)
        },
        WizardStep(R.string.title_character_creation_points, points) {
            PointsPoolForm(it, validate = validate.value)
        }
    )

    Column(Modifier.fillMaxSize()) {
        ScrollableColumn(Modifier.weight(1f).background(MaterialTheme.colors.background)) {
            val currentStep = steps[currentStepIndex.value]

            SubheadBar(stringResource(currentStep.labelRes))

            Box(Modifier.padding(24.dp)) { currentStep.render() }
        }

        BottomBar(
            steps = steps,
            currentStepIndex = currentStepIndex.value,
            onChange = { newIndex, newValidate ->
                currentStepIndex.value = newIndex
                validate.value = newValidate
            },
            onFinish = { saveCharacter() },
            formState = formState.value,
        )
    }
}

@Composable
private fun BottomBar(
    steps: List<WizardStep<*>>,
    currentStepIndex: Int,
    onChange: (newIndex: Int, validate: Boolean) -> Unit,
    onFinish: () -> Unit,
    formState: FormState,
) {
    val currentStep = steps[currentStepIndex]

    Surface(
        elevation = 4.dp,
        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)) {
            val buttonModifier = Modifier.padding(8.dp)

            if (currentStepIndex > 0) {
                TextButton(
                    modifier = buttonModifier.align(Alignment.TopStart),
                    onClick = {
                        onChange(currentStepIndex - 1, false)
                    },
                ) {
                    Icon(vectorResource(R.drawable.ic_caret_left))
                    Text(
                        stringResource(steps[currentStepIndex - 1].labelRes)
                            .toUpperCase(Locale.current),
                        color = MaterialTheme.colors.onSurface,
                    )
                }
            }

            if (currentStepIndex == steps.lastIndex) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (formState == FormState.CREATING_CHARACTER) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    }

                    NextButton(R.string.button_finish, buttonModifier, onClick = {
                        if (!currentStep.data.isValid()) {
                            onChange(currentStepIndex, true)
                            return@NextButton
                        }

                        onFinish()
                    })
                }
            } else {
                NextButton(
                    steps[currentStepIndex + 1].labelRes,
                    modifier = buttonModifier.align(Alignment.TopEnd),
                    onClick = {
                        if (!currentStep.data.isValid()) {
                            onChange(currentStepIndex, true)
                            return@NextButton
                        }

                        onChange(currentStepIndex + 1, false)
                    },
                )
            }
        }
    }
}

private data class WizardStep<T : FormData>(
    @StringRes val labelRes: Int,
    val data: T,
    val form: @Composable (T) -> Unit,
) {
    @Composable
    fun render() {
        form(data)
    }
}

@Composable
private fun NextButton(
    @StringRes label: Int,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    TextButton(modifier = modifier, onClick = onClick) {
        Text(stringResource(label).toUpperCase(Locale.current))
        Icon(vectorResource(R.drawable.ic_caret_right))
    }
}