package cz.frantisekmasa.wfrp_master.common.character.traits

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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.TraitSpecificationsForm
import cz.frantisekmasa.wfrp_master.common.compendium.trait.CompendiumTraitDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import dev.icerock.moko.resources.compose.stringResource

class CharacterTraitDetailScreen(characterId: CharacterId, traitId: Uuid) :
    CharacterItemDetailScreen(characterId, traitId) {

    @Composable
    override fun Content() {
        val screenModel: TraitsScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { trait, isGameMaster ->
            val navigation = LocalNavigationTransaction.current
            var edit by rememberSaveable { mutableStateOf(false) }

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
                    onDismissRequest = navigation::goBack,
                    subheadBar = {
                        if (isGameMaster) {
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
                                stringResource(Str.traits_title_edit),
                                onClick = { edit = true }
                            )
                        }
                    }
                )
            }
        }
    }
}
