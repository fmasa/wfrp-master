package cz.frantisekmasa.wfrp_master.common.skillTest

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

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
            items(results, key = { it.characterId }) { result ->
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
                    stringResource(Str.tests_cannot_test_against_unknown_advanced_skill),
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
                    stringResource(Str.tests_roll),
                    buildString {
                        append(
                            stringResource(
                                Str.tests_roll_label,
                                testResult.rollValue,
                                testResult.testedValue,
                            )
                        )

                        if (testResult.isFumble) {
                            append(stringResource(Str.tests_fumble))
                        } else if (testResult.isCritical) {
                            append(stringResource(Str.tests_critical))
                        }
                    }
                )

                SingleLineTextValue(
                    stringResource(Str.tests_success_level_shortcut),
                    buildString {
                        append(testResult.successLevelText)
                        append(" (")
                        append(testResult.dramaticResult.localizedName)
                        append(')')
                    },
                )

                CardButton(
                    stringResource(Str.tests_button_reroll),
                    onClick = onRerollRequest,
                )
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

                CardButton(
                    stringResource(Str.tests_button_reroll),
                    onClick = onRerollRequest,
                )
            }
        }
    }
}
