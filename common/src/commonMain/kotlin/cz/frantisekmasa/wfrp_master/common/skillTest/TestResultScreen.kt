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
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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

        val strings = LocalStrings.current

        when (val roll = result.roll) {
            Roll.CharacterDoesNotHaveAdvances -> {
                Text(
                    LocalStrings.current.tests.cannotTestAgainstUnknownAdvancedSkill,
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
                    strings.tests.roll,
                    strings.tests.rollLabel(testResult.rollValue, testResult.testedValue).let {
                        when {
                            testResult.isFumble -> "$it (${strings.tests.fumble})"
                            testResult.isCritical -> "$it (${strings.tests.critical})"
                            else -> it
                        }
                    }
                )

                SingleLineTextValue(
                    strings.tests.successLevelShortcut,
                    testResult.successLevelText,
                )

                SingleLineTextValue(
                    strings.tests.labelDramaticResult,
                    testResult.dramaticResult.localizedName,
                )

                CardButton(strings.tests.buttonReroll, onClick = onRerollRequest)
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

                CardButton(strings.tests.buttonReroll, onClick = onRerollRequest)
            }
        }
    }
}
