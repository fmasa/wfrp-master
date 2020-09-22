package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.composables.FormData
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.viewModels.CharacterCreationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalArgumentException
import java.util.*

class CharacterCreationFragment(
    private val characters: CharacterRepository
) : PartyScopedFragment(0),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: CharacterCreationFragmentArgs by navArgs()

    private val viewModel: CharacterCreationViewModel by viewModel { parametersOf(args.partyId) }

    override fun onStart() {
        super.onStart()

        launch {
            val userId = args.userId
            if (userId != null && characters.hasCharacterInParty(userId, args.partyId)) {
                withContext(Dispatchers.Main) { toast(R.string.already_has_character) }
                return@launch
            }
        }
    }

    @ExperimentalLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                CharacterCreationScreen(
                    this@CharacterCreationFragment,
                    viewModel,
                    args.userId,
                    onCharacterCreated = {
                        val navController = findNavController()
                        navController.navigate(
                            CharacterCreationFragmentDirections.openCharacter(it),
                            navOptions {
                                popUpTo(
                                    try {
                                        navController.getBackStackEntry(R.id.nav_game_master)
                                        R.id.nav_game_master
                                    } catch (e: IllegalArgumentException) {
                                        R.id.nav_party_list
                                    }
                                ) { inclusive = false }
                            }
                        )
                    }
                )
            }
        }
    }

    override fun getPartyId(): UUID = args.partyId
    private fun saveCharacter(
        basicInfo: CharacterBasicInfoForm.Data,
        characteristics: CharacterCharacteristicsForm.Data,
        points: PointsPoolForm.Data,
    ) {
        launch {
            val characterId =
                viewModel.createCharacter(args.userId, basicInfo, characteristics, points)

            withContext(Dispatchers.Main) {
                toast("Your character has been created")

                val navController = findNavController()
                findNavController()
                    .navigate(
                        CharacterCreationFragmentDirections.openCharacter(characterId),
                        navOptions {
                            popUpTo(
                                try {
                                    navController.getBackStackEntry(R.id.nav_game_master)
                                    R.id.nav_game_master
                                } catch (e: IllegalArgumentException) {
                                    R.id.nav_party_list
                                }
                            ) { inclusive = false }
                        }
                    )
            }
        }

    }
}

private enum class FormState {
    EDITED_BY_USER,
    CREATING_CHARACTER,
}

@ExperimentalLayout
@Composable
fun CharacterCreationScreen(
    coroutineScope: CoroutineScope,
    viewModel: CharacterCreationViewModel,
    userId: String?,
    onCharacterCreated: (CharacterId) -> Unit,
) {
    val validate = remember { mutableStateOf(false) }
    val currentStepIndex = savedInstanceState { 0 }
    val formState = savedInstanceState { FormState.EDITED_BY_USER }

    val basicInfo = CharacterBasicInfoForm.Data.empty()
    val characteristics = CharacterCharacteristicsForm.Data.empty()
    val points = PointsPoolForm.Data.empty()


    val saveCharacter = {
        formState.value = FormState.CREATING_CHARACTER
        coroutineScope.launch {
            val characterId = viewModel.createCharacter(userId, basicInfo, characteristics, points)

            withContext(Dispatchers.Main) { onCharacterCreated(characterId) }
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

            Surface(elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(currentStep.labelRes),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

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
        color = EmphasisAmbient.current.medium.applyEmphasis(MaterialTheme.colors.onSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Stack(Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)) {
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