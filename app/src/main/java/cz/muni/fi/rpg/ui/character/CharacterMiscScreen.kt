package cz.muni.fi.rpg.ui.character

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.ui.common.composables.AmbitionsCard
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.viewModels.CharacterMiscViewModel

@Composable
fun CharacterMiscScreen(
    character: Character,
    modifier: Modifier = Modifier,
    viewModel: CharacterMiscViewModel,
    onXpButtonClick: (Int) -> Unit,
    onCharacterAmbitionsClick: (Ambitions) -> Unit
) {
    ScrollableColumn(modifier.background(MaterialTheme.colors.background)) {
        MainCard(character)
        XpPointsCard(character.getPoints().experience, onChangeButtonClicked = onXpButtonClick)

        AmbitionsCard(
            R.string.title_character_ambitions,
            character.getAmbitions(),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable(onClick = { onCharacterAmbitionsClick(character.getAmbitions()) })
        )

        viewModel.party.observeAsState().value?.let {
            AmbitionsCard(
                R.string.title_party_ambitions,
                it.getAmbitions(),
                R.drawable.ic_group,
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
    }
}