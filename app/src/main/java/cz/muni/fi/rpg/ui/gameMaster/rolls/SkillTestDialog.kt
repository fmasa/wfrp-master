package cz.muni.fi.rpg.ui.gameMaster.rolls

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.TestResult
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.muni.fi.rpg.viewModels.SkillTestViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Composable
fun SkillTestDialog(partyId: PartyId, onDismissRequest: () -> Unit) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val viewModel: SkillTestViewModel by viewModel { parametersOf(partyId) }
        var step: SkillTestDialogStep by rememberSaveable { mutableStateOf(SkillTestDialogStep.SkillPicking) }

        when (val currentStep = step) {
            SkillTestDialogStep.SkillPicking -> SkillChooser(
                viewModel = viewModel,
                onDismissRequest = onDismissRequest,
                onSkillSelected = { step = SkillTestDialogStep.Options(selectedSkill = it) }
            )
            is SkillTestDialogStep.Options -> {
                val coroutineScope = rememberCoroutineScope()

                OptionsForm(
                    viewModel = viewModel,
                    onDismissRequest = onDismissRequest,
                    selectedSkill = currentStep.selectedSkill,
                    onNewSkillPickingRequest = { step = SkillTestDialogStep.SkillPicking },
                    onExecute = { characters, testModifier ->
                        coroutineScope.launch(Dispatchers.IO) {
                            val results = characters.map {
                                async {
                                    val testResult = viewModel.performSkillTest(
                                        it,
                                        currentStep.selectedSkill,
                                        testModifier,
                                    )

                                    RollResult(
                                        characterId = it.id,
                                        characterName = it.name,
                                        roll = if (testResult != null)
                                            Roll.Test(currentStep.selectedSkill.name, testResult)
                                        else Roll.CharacterDoesNotHaveAdvances,
                                    )
                                }
                            }

                            step = SkillTestDialogStep.ShowResults(
                                testName = currentStep.selectedSkill.name,
                                results = results.awaitAll()
                            )
                        }
                    }
                )
            }
            is SkillTestDialogStep.ShowResults -> {
                TestResultScreen(
                    testName = LocalStrings.current.tests.titleTest(currentStep.testName),
                    onDismissRequest = onDismissRequest,
                    results = currentStep.results,
                    onRerollRequest = { step = currentStep.rerollForCharacter(it) }
                )
            }
        }
    }
}

private sealed class SkillTestDialogStep : Parcelable {
    @Parcelize
    object SkillPicking : SkillTestDialogStep()

    @Parcelize
    data class Options(val selectedSkill: Skill) : SkillTestDialogStep()

    @Parcelize
    data class ShowResults(
        val testName: String,
        val results: List<RollResult>
    ) : SkillTestDialogStep() {
        fun rerollForCharacter(characterId: String) = copy(
            results = results.map { if (it.characterId == characterId) it.reroll() else it }
        )
    }
}

@Parcelize
@Immutable
internal data class RollResult(
    val characterId: String,
    val characterName: String,
    val roll: Roll,
) : Parcelable {
    fun reroll() = copy(roll = roll.reroll())
}

@Immutable
internal sealed class Roll : Parcelable {
    abstract fun reroll(): Roll

    @Parcelize
    @Immutable
    data class Generic(
        val expression: Expression,
        val rolledValue: Int,
    ) : Roll() {
        override fun reroll(): Roll = copy(rolledValue = expression.evaluate())
    }

    @Parcelize
    @Immutable
    data class Test(
        val testName: String,
        val result: TestResult
    ) : Roll() {
        override fun reroll() = copy(result = result.copy(rollValue = dice.roll()))

        companion object {
            val dice = Dice(100)
        }
    }

    @Parcelize
    @Immutable
    object CharacterDoesNotHaveAdvances : Roll() {
        override fun reroll() = this
    }
}
