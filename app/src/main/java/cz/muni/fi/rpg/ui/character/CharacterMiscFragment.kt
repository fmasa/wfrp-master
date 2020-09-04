package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.CharacterMiscViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class CharacterMiscFragment : Fragment(),
    CoroutineScope by CoroutineScope(
        Dispatchers.Default
    ) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterMiscFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: CharacterMiscViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Theme {
                    MainContainer(
                        viewModel,
                        onXpButtonClick = ::openExperiencePointsDialog,
                        onCharacterAmbitionsClick = { defaults ->
                            ChangeAmbitionsDialog
                                .newInstance(
                                    getString(R.string.title_character_ambitions),
                                    defaults
                                )
                                .setOnSaveListener { viewModel.updateCharacterAmbitions(it) }
                                .show(childFragmentManager, "ChangeAmbitionsDialog")
                        },
                    )
                }
            }
        }
    }

    private fun openExperiencePointsDialog(currentXpPoints: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_xp, null, false)

        val xpPointsInput = view.findViewById<TextInput>(R.id.xpPointsInput)
        xpPointsInput.setDefaultValue(currentXpPoints.toString())

        AlertDialog.Builder(requireContext(), R.style.FormDialog)
            .setTitle("Change amount of XP")
            .setView(view)
            .setPositiveButton(R.string.button_save) { _, _ ->
                val xpPoints = xpPointsInput.getValue().toIntOrNull() ?: 0
                launch { viewModel.updateExperiencePoints(xpPoints) }
            }.create()
            .show()
    }
}

@Composable
private fun MainContainer(
    viewModel: CharacterMiscViewModel,
    onXpButtonClick: (Int) -> Unit,
    onCharacterAmbitionsClick: (Ambitions) -> Unit
) {
    val character = viewModel.character.observeAsState().value ?: return

    ScrollableColumn(Modifier.background(MaterialTheme.colors.background)) {
        MainCard(character)
        XpPointsCard(character.getPoints().experience, onChangeButtonClicked = onXpButtonClick)

        AmbitionsCard(
            R.string.title_character_ambitions,
            character.getAmbitions(),
            modifier = Modifier.clickable(
                onClick = { onCharacterAmbitionsClick(character.getAmbitions()) }
            )
        )

        viewModel.party.observeAsState().value?.let {
            AmbitionsCard(R.string.title_party_ambitions, it.getAmbitions(), R.drawable.ic_group)
        }

        Spacer(Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun AmbitionsCard(
    @StringRes titleRes: Int,
    ambitions: Ambitions,
    @DrawableRes titleIconRes: Int? = null,
    modifier: Modifier = Modifier,
) {
    Card(modifier) {
        CardTitle(titleRes, titleIconRes)

        for ((label, value) in listOf(
            R.string.label_ambition_short_term to ambitions.shortTerm,
            R.string.label_ambition_long_term to ambitions.longTerm
        )) {
            Column(Modifier.padding(top = 4.dp)) {
                Text(
                    stringResource(label),
                    fontWeight = FontWeight.Bold
                )

                if (value.isBlank()) {
                    ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                        Row(verticalGravity = Alignment.CenterVertically) {
                            Icon(vectorResource(R.drawable.ic_none))
                            Text(
                                stringResource(R.string.note_ambition_not_filled),
                                style = MaterialTheme.typography.body2,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                } else {
                    Text(ambitions.shortTerm)
                }
            }
        }
    }
}

@Composable
private fun MainCard(character: Character) {
    Card {
        Column(Modifier.fillMaxWidth()) {
            CardTitle(character.getName())
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                SingleLineTextValue(
                    R.string.label_race,
                    stringResource(character.getRace().getReadableNameId())
                )
                SingleLineTextValue(R.string.label_career, character.getCareer())
                SingleLineTextValue(R.string.label_social_class, character.getSocialClass())
                MultiLineTextValue(R.string.label_psychology, character.getPsychology())
                MultiLineTextValue(R.string.label_motivation, character.getMotivation())
                MultiLineTextValue(R.string.label_character_note, character.getNote())
            }
        }
    }
}

@Composable
private fun MultiLineTextValue(@StringRes labelRes: Int, value: String) {
    if (value.isBlank()) return

    Column {
        Text(stringResource(labelRes), fontWeight = FontWeight.Bold)
        Text(value)
    }
}

@Composable
private fun SingleLineTextValue(@StringRes labelRes: Int, value: String) {
    if (value.isBlank()) return

    Row {
        Text(
            stringResource(labelRes) + ":",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(value)
    }
}

@Composable
private fun XpPointsCard(xpPoints: Int, onChangeButtonClicked: (Int) -> Unit) {
    Card(Modifier.clickable(onClick = { onChangeButtonClicked(xpPoints) })) {
        SingleLineTextValue(R.string.xp_points, xpPoints.toString())
    }
}

@Composable
private fun Card(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp).then(modifier)) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            content()
        }
    }
}