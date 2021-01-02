package cz.muni.fi.rpg.ui.gameMaster.rolls

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.core.domain.rolls.TestResult
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.viewModel.newViewModel
import cz.muni.fi.rpg.viewModels.SkillTestViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf
import java.util.*

private val dice = Dice(100)

@Composable
fun SkillTestDialog(partyId: UUID, onDismissRequest: () -> Unit) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val viewModel: SkillTestViewModel = newViewModel { parametersOf(partyId) }
        var step: SkillTestDialogStep by savedInstanceState { SkillTestDialogStep.SkillPicking }

        when (val currentStep = step) {
            SkillTestDialogStep.SkillPicking -> SkillChooser(
                viewModel = viewModel,
                onDismissRequest = onDismissRequest,
                onSkillSelected = { step = SkillTestDialogStep.Options(selectedSkill = it) })
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
                                    SkillTestResult(
                                        characterId = it.id,
                                        characterName = it.getName(),
                                        testName = currentStep.selectedSkill.name,
                                        testResult = viewModel.performSkillTest(
                                            it,
                                            currentStep.selectedSkill,
                                            testModifier,
                                        ),
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
                    testName = currentStep.testName,
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
        val results: List<SkillTestResult>
    ) : SkillTestDialogStep() {
        fun rerollForCharacter(characterId: String) = copy(
            results = results.map { if (it.characterId == characterId) it.reroll() else it }
        )
    }
}

@Parcelize
internal data class SkillTestResult(
    val characterId: String,
    val characterName: String,
    val testName: String,
    val testResult: TestResult?,
) : Parcelable {
    fun reroll() = copy(testResult = testResult?.copy(rollValue = dice.roll()))
}