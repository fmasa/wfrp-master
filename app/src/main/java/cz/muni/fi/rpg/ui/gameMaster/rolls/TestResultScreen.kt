package cz.muni.fi.rpg.ui.gameMaster.rolls

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.CardButton
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.CardTitle

@Composable
internal fun TestResultScreen(
    testName: String,
    results: List<SkillTestResult>,
    onRerollRequest: (characterId: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onDismissRequest) },
                title = { Text(stringResource(R.string.title_test, testName)) },
            )
        },
    ) {
        ScrollableColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
            for (result in results) {
                TestResultCard(
                    testName = testName,
                    result = result,
                    onRerollRequest = { onRerollRequest(result.characterId) }
                )
            }
        }
    }
}

@Composable
private fun TestResultCard(
    result: SkillTestResult,
    testName: String,
    onRerollRequest: () -> Unit,
) {
    CardContainer(Modifier.fillMaxWidth(), bodyPadding = PaddingValues(start = 8.dp, end = 8.dp)) {
        CardTitle(result.characterName)

        val testResult = result.testResult

        if (testResult == null) {
            Text(
                stringResource(R.string.error_needs_skill_advance),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            return@CardContainer
        }

        Text(
            stringResource(R.string.title_test, testName),
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        SingleLineTextValue(
            R.string.item_roll,
            stringResource(R.string.item_roll_value, testResult.rollValue, testResult.testedValue)
                .let {
                    when {
                        testResult.isFumble -> it + " (" + stringResource(R.string.item_fumble) + ")"
                        testResult.isCritical -> it + " (" + stringResource(R.string.item_critical) + ")"
                        else -> it
                    }
                }
        )

        SingleLineTextValue(
            R.string.item_success_level,
            testResult.successLevel,
        )

        SingleLineTextValue(
            R.string.item_dramatic_result,
            stringResource(testResult.dramaticResult.labelResource)
        )
        
        CardButton(R.string.button_test_reroll, onClick = onRerollRequest)
    }
}