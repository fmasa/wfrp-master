package cz.muni.fi.rpg.ui.character.edit

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.characterCreation.CharacterBasicInfoForm
import cz.muni.fi.rpg.ui.characterCreation.CharacterCharacteristicsForm
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class CharacterEditFragment : PartyScopedFragment(0),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: CharacterEditFragmentArgs by navArgs()

    private val viewModel: CharacterViewModel by viewModel { parametersOf(args.characterId) }

    @ExperimentalLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                CharacterEditScreen(
                    coroutineScope = this@CharacterEditFragment,
                    viewModel = viewModel,
                    onBack = {
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    override fun getPartyId(): UUID = args.characterId.partyId
}

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
        val maxWounds: MutableState<String>,
        val hardyTalent: MutableState<Boolean>,
    ) {
        companion object {
            @Composable
            fun fromCharacter(character: Character) =
                WoundsData(
                    maxWounds = savedInstanceState { character.getPoints().maxWounds.toString() },
                    hardyTalent = savedInstanceState { character.hasHardyTalent() }
                )
        }

        fun isValid() = maxWounds.value.toIntOrNull()?.let { it > 0 } ?: false
    }
}

@ExperimentalLayout
@Composable
fun CharacterEditScreen(
    coroutineScope: CoroutineScope,
    viewModel: CharacterViewModel,
    onBack: () -> Unit
) {
    val character = viewModel.character.right().observeAsState().value

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

                        coroutineScope.launch {
                            updateCharacter(viewModel, formData)
                            withContext(Dispatchers.Main) { onBack() }
                        }
                    } else {
                        validate.value = true
                    }
                },
                onBack = onBack,
                actionsEnabled = submitEnabled.value && character != null
            )
        }
    ) {
        formData?.let { CharacterEditMainUI(it, validate.value) }
    }
}

@ExperimentalLayout
@Composable
private fun CharacterEditMainUI(formData: CharacterEditScreen.FormData, validate: Boolean) {
    ScrollableColumn(Modifier.fillMaxSize()) {
        Column(Modifier.padding(24.dp)) {
            CharacterBasicInfoForm(formData.basicInfo, validate)

            HorizontalLine()

            MaxWoundsSegment(formData.wounds, validate)

            HorizontalLine()

            Text(
                stringResource(R.string.title_character_characteristics),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp).fillMaxWidth()
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
            modifier = Modifier.fillMaxWidth(0.3f).padding(bottom = 12.dp),
            value = data.maxWounds.value,
            maxLength = 3,
            keyboardType = KeyboardType.Number,
            onValueChange = { data.maxWounds.value = it },
            validate = validate,
            rules = Rules(
                Rules.NotBlank(),
                { v: String -> v.toInt() > 0 } to stringResource(R.string.error_value_is_0),
            )
        )

        CheckboxWithText(
            text = stringResource(R.string.title_checkbox_hardy),
            checked = data . hardyTalent . value,
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
    val emphasis = EmphasisAmbient.current
    TopAppBar(
        navigationIcon = { BackButton(onBack) },
        title = {
            Column {
                Text(title)
                Text(
                    stringResource(R.string.subtitle_edit_character),
                    style = MaterialTheme.typography.caption,
                    color = emphasis.medium.applyEmphasis(contentColor()),
                )
            }
        },
        actions = {
            TopBarAction(onClick = onSave, enabled = actionsEnabled) {
                Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
            }
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