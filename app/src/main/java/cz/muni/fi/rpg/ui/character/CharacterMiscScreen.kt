package cz.muni.fi.rpg.ui.character

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.CharacterMiscViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
internal fun CharacterMiscScreen(
    characterId: CharacterId,
    character: Character,
    modifier: Modifier = Modifier,
) {
    val context = ContextAmbient.current
    val viewModel: CharacterMiscViewModel by viewModel { parametersOf(characterId) }
    val coroutineScope = rememberCoroutineScope()

    ScrollableColumn(modifier.background(MaterialTheme.colors.background)) {
        MainCard(character)
        XpPointsCard(
            character.getPoints().experience,
            onChangeButtonClicked = { xpPoints ->
                with(coroutineScope) { openExperiencePointsDialog(context, viewModel, xpPoints) }
            }
        )

        val fragmentManager = fragmentManager()
        val ambitionsDialogTitle = stringResource(R.string.title_character_ambitions)

        AmbitionsCard(
            titleRes = R.string.title_character_ambitions,
            ambitions = character.getAmbitions(),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable(onClick = {
                    ChangeAmbitionsDialog
                        .newInstance(ambitionsDialogTitle, character.getAmbitions())
                        .setOnSaveListener { viewModel.updateCharacterAmbitions(it) }
                        .show(fragmentManager, "ChangeAmbitionsDialog")
                })
        )

        viewModel.party.collectAsState(null).value?.let {
            AmbitionsCard(
                titleRes = R.string.title_party_ambitions,
                ambitions = it.getAmbitions(),
                titleIconRes = R.drawable.ic_group,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(Modifier.padding(bottom = 20.dp))
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
        R.drawable.common_google_signin_btn_icon_dark
    }
}


private fun CoroutineScope.openExperiencePointsDialog(
    context: Context,
    viewModel: CharacterMiscViewModel,
    currentXpPoints: Int
) {
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_xp, null, false)

    val xpPointsInput = view.findViewById<TextInput>(R.id.xpPointsInput)
    xpPointsInput.setDefaultValue(currentXpPoints.toString())

    AlertDialog.Builder(context, R.style.FormDialog)
        .setTitle("Change amount of XP")
        .setView(view)
        .setPositiveButton(R.string.button_save) { _, _ ->
            val xpPoints = xpPointsInput.getValue().toIntOrNull() ?: 0
            launch(Dispatchers.IO) { viewModel.updateExperiencePoints(xpPoints) }
        }.create()
        .show()
}