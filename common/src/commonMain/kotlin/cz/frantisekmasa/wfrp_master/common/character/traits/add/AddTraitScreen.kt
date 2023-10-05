package cz.frantisekmasa.wfrp_master.common.character.traits.add

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.TraitSpecificationsForm
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTraitScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddTraitScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        val (step, setStep) = rememberSaveable {
            mutableStateOf<Step>(Step.ChoosingCompendiumTrait)
        }

        if (state == null) {
            FullScreenProgress()
            return
        }

        when (step) {
            Step.ChoosingCompendiumTrait -> {
                val coroutineScope = rememberCoroutineScope()
                var saving by remember { mutableStateOf(false) }

                if (saving) {
                    Surface {
                        FullScreenProgress()
                    }
                    return
                }

                val navigation = LocalNavigationTransaction.current

                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.traits_title_choose_compendium_trait),
                    onDismissRequest = { navigation.goBack() },
                    icon = { Resources.Drawable.Trait },
                    onSelect = {
                        if (it.specifications.isEmpty()) {
                            saving = true
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.saveNewTrait(it.id, emptyMap())
                                navigation.goBack()
                            }
                        } else {
                            setStep(Step.FillingInTimesSpecifications(it))
                        }
                    },
                    emptyUiIcon = Resources.Drawable.Trait,
                )
            }
            is Step.FillingInTimesSpecifications ->
                TraitSpecificationsForm(
                    existingTrait = null,
                    onSave = { screenModel.saveNewTrait(step.compendiumTrait.id, it) },
                    onDismissRequest = { setStep(Step.ChoosingCompendiumTrait) },
                    defaultSpecifications = step.compendiumTrait.specifications.associateWith { "" },
                )
        }
    }

    private sealed class Step : Parcelable {
        @Parcelize
        class FillingInTimesSpecifications(val compendiumTrait: Trait) : Step()

        @Parcelize
        object ChoosingCompendiumTrait : Step()
    }
}
