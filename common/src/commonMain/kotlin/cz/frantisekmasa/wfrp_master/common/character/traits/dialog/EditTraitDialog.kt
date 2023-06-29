package cz.frantisekmasa.wfrp_master.common.character.traits.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitDetail
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.trait.CompendiumTraitDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun EditTraitDialog(
    screenModel: TraitsScreenModel,
    traitId: Uuid,
    onDismissRequest: () -> Unit,
) {
    val trait = screenModel.items.collectWithLifecycle(null).value
        ?.firstOrNull { it.id == traitId }
        ?: return

    var edit by rememberSaveable { mutableStateOf(false) }

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (edit) {
            TraitSpecificationsForm(
                existingTrait = trait,
                defaultSpecifications = trait.specificationValues,
                compendiumTraitId = trait.compendiumId,
                screenModel = screenModel,
                onDismissRequest = { edit = false },
            )
        } else {
            TraitDetail(
                trait,
                onDismissRequest = onDismissRequest,
                subheadBar = {
                    val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(false).value

                    if (isGameMaster) {
                        val navigation = LocalNavigationTransaction.current

                        CompendiumButton(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = Spacing.bodyPadding),
                            onClick = {
                                navigation.navigate(
                                    CompendiumTraitDetailScreen(
                                        screenModel.characterId.partyId,
                                        trait.compendiumId,
                                    )
                                )
                            }
                        )
                    }
                },
                actions = {
                    if (trait.specificationValues.isNotEmpty()) {
                        IconAction(
                            Icons.Rounded.Edit,
                            LocalStrings.current.traits.titleEdit,
                            onClick = { edit = true }
                        )
                    }
                }
            )
        }
    }
}
