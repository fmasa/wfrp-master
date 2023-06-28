package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

class CompendiumListScreen(
    private val partyId: PartyId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: PartyScreenModel = rememberScreenModel(arg = partyId)

        val strings = LocalStrings.current.compendium
        val navigation = LocalNavigationTransaction.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(strings.title)
                            screenModel.party.collectWithLifecycle(null).value?.let {
                                Subtitle(it.name)
                            }
                        }
                    },
                    navigationIcon = { BackButton() },
                    actions = {
                        OptionsAction {
                            DropdownMenuItem(
                                onClick = {
                                    navigation.navigate(RulebookCompendiumImportScreen(partyId))
                                }
                            ) {
                                Text(strings.buttonImportFromRulebook)
                            }

                            DropdownMenuItem(
                                onClick = {
                                    navigation.navigate(JsonCompendiumImportScreen(partyId))
                                }
                            ) {
                                Text(strings.buttonImportFile)
                            }

                            DropdownMenuItem(
                                onClick = {
                                    navigation.navigate(JsonCompendiumExportScreen(partyId))
                                }
                            ) {
                                Text(strings.buttonExportFile)
                            }
                        }
                    }
                )
            }
        ) {
            CardContainer(
                Modifier
                    .padding(top = 6.dp)
                    .padding(horizontal = Spacing.small),
            ) {
                Column {
                    val types = CompendiumScreen.Type.values()

                    types.forEachIndexed { index, compendiumType ->
                        CardItem(
                            name = compendiumType.localizedName,
                            icon = { ItemIcon(icon(compendiumType)) },
                            onClick = { navigation.navigate(compendiumType.screen(partyId)) },
                            showDivider = index != types.lastIndex,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun icon(compendiumType: CompendiumScreen.Type): Resources.Drawable {
        return when (compendiumType) {
            CompendiumScreen.Type.BLESSINGS -> Resources.Drawable.Blessing
            CompendiumScreen.Type.MIRACLES -> Resources.Drawable.Miracle
            CompendiumScreen.Type.SKILLS -> Resources.Drawable.Skill
            CompendiumScreen.Type.TALENTS -> Resources.Drawable.Talent
            CompendiumScreen.Type.SPELLS -> Resources.Drawable.Spell
            CompendiumScreen.Type.TRAITS -> Resources.Drawable.Trait
            CompendiumScreen.Type.CAREERS -> Resources.Drawable.Career
        }
    }
}
