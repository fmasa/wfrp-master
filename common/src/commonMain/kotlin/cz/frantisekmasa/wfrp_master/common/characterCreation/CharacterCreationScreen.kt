package cz.frantisekmasa.wfrp_master.common.characterCreation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class FormState {
    EDITED_BY_USER,
    CREATING_CHARACTER,
}

class CharacterCreationScreen(
    val partyId: PartyId,
    val type: CharacterType,
    val userId: UserId?,
) : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(LocalStrings.current.characterCreation.title) },
                    navigationIcon = { BackButton() }
                )
            }
        ) {
            MainContainer(partyId, type, userId)
        }
    }
}

@Composable
private fun Screen.MainContainer(partyId: PartyId, type: CharacterType, userId: UserId?) {
    val screenModel: CharacterCreationScreenModel = rememberScreenModel(arg = partyId)
    val coroutineScope = rememberCoroutineScope()

    val (careers, setCareers) = rememberSaveable {
        mutableStateOf<List<Career>?>(null)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setCareers(screenModel.getCareers())
        }
    }

    if (careers == null) {
        FullScreenProgress()
        return
    }

    val validate = remember { mutableStateOf(false) }
    val currentStepIndex = rememberSaveable { mutableStateOf(0) }
    val formState = rememberSaveable { mutableStateOf(FormState.EDITED_BY_USER) }

    val basicInfo = CharacterBasicInfoForm.Data.empty(type, careers)
    val characteristics = CharacterCharacteristicsForm.Data.fromCharacter(null)
    val points = PointsPoolForm.Data.empty()

    val navigation = LocalNavigationTransaction.current
    val saveCharacter = {
        formState.value = FormState.CREATING_CHARACTER
        coroutineScope.launch(Dispatchers.IO) {
            val characterId = screenModel.createCharacter(
                userId,
                type,
                basicInfo,
                characteristics,
                points
            )

            navigation.replace(CharacterDetailScreen(characterId))
        }
    }

    val labels = LocalStrings.current.characterCreation

    val steps = listOf(
        WizardStep(labels.stepBasicInfo, basicInfo) {
            CharacterBasicInfoForm(it, validate = validate.value)
        },
        WizardStep(labels.stepAttributes, characteristics) {
            CharacterCharacteristicsForm(it, validate = validate.value)
        },
        WizardStep(labels.stepPointPools, points) {
            PointsPoolForm(it, validate = validate.value)
        }
    )

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState())
        ) {
            SubheadBar(steps[currentStepIndex.value].label)

            AnimatedContent(
                targetState = currentStepIndex.value,
                transitionSpec = {
                    if (initialState == targetState) {
                        fadeIn(snap(), 0f) with fadeOut(snap(), 0f)
                    } else {
                        val direction = if (targetState > initialState)
                            AnimatedContentScope.SlideDirection.Start
                        else AnimatedContentScope.SlideDirection.End
                        val animationSpec = tween<IntOffset>(250)
                        slideIntoContainer(direction, animationSpec) with
                            slideOutOfContainer(direction, animationSpec)
                    }
                },
            ) {
                Box(Modifier.fillMaxWidth().padding(24.dp)) {
                    steps[it].render()
                }
            }
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
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            val buttonModifier = Modifier.padding(8.dp)

            if (currentStepIndex > 0) {
                TextButton(
                    modifier = buttonModifier.align(Alignment.TopStart),
                    onClick = {
                        onChange(currentStepIndex - 1, false)
                    },
                ) {
                    Icon(
                        Icons.Rounded.ArrowBackIos,
                        VisualOnlyIconDescription,
                        tint = MaterialTheme.colors.primaryVariant,
                    )
                    Text(
                        steps[currentStepIndex - 1].label.uppercase(),
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

                    NextButton(
                        LocalStrings.current.commonUi.buttonFinish,
                        buttonModifier,
                        onClick = {
                            if (!currentStep.data.isValid()) {
                                onChange(currentStepIndex, true)
                                return@NextButton
                            }

                            onFinish()
                        }
                    )
                }
            } else {
                NextButton(
                    steps[currentStepIndex + 1].label.uppercase(),
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
    val label: String,
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
    label: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    TextButton(modifier = modifier, onClick = onClick) {
        Text(label.uppercase())
        Icon(
            Icons.Rounded.ArrowForwardIos,
            VisualOnlyIconDescription,
            tint = MaterialTheme.colors.primaryVariant,
        )
    }
}
