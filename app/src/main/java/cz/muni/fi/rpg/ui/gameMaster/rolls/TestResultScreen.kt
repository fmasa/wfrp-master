package cz.muni.fi.rpg.ui.gameMaster.rolls

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.CardTitle

@Composable
internal fun TestResultScreen(
    testName: String,
    results: List<RollResult>,
    onRerollRequest: (characterId: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onDismissRequest) },
                title = { Text(testName) },
            )
        },
    ) {
        LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
            items(results) { result ->
                TestResultCard(
                    result = result,
                    onRerollRequest = { onRerollRequest(result.characterId) }
                )
            }
        }
    }
}

@Composable
private fun TestResultCard(
    result: RollResult,
    onRerollRequest: () -> Unit,
) {
    CardContainer(Modifier.fillMaxWidth(), bodyPadding = PaddingValues(start = 8.dp, end = 8.dp)) {
        CardTitle(result.characterName)

        when (val roll = result.roll) {
            Roll.CharacterDoesNotHaveAdvances -> {
                Text(
                    stringResource(R.string.error_needs_skill_advance),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            is Roll.Test -> {
                Text(
                    roll.testName,
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                val testResult = roll.result

                SingleLineTextValue(
                    R.string.item_roll,
                    stringResource(
                        R.string.item_roll_value,
                        testResult.rollValue,
                        testResult.testedValue.toString()
                    ).let {
                        when {
                            testResult.isFumble -> it + " (" + stringResource(R.string.item_fumble) + ")"
                            testResult.isCritical -> it + " (" + stringResource(R.string.item_critical) + ")"
                            else -> it
                        }
                    }
                )

                SingleLineTextValue(
                    R.string.item_success_level,
                    testResult.successLevelText,
                )

                SingleLineTextValue(
                    R.string.item_dramatic_result,
                    testResult.dramaticResult.localizedName,
                )

                CardButton(R.string.button_test_reroll, onClick = onRerollRequest)
            }
            is Roll.Generic -> {
                Text(
                    roll.expression.toString(),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Text(
                    roll.rolledValue.toString(),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                CardButton(R.string.button_test_reroll, onClick = onRerollRequest)
            }
        }
    }
}
