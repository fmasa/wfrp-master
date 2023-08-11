package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.blessing.BlessingCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.career.CareerCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.miracle.MiracleCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.skill.SkillCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.talent.TalentCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.trait.TraitCompendiumScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

abstract class CompendiumScreen() : Screen {

    enum class Type(
        override val translatableName: StringResource,
        val screen: (PartyId) -> Screen,
    ) : NamedEnum {
        SKILLS(Str.compendium_title_skills, ::SkillCompendiumScreen),
        TALENTS(Str.compendium_title_talents, ::TalentCompendiumScreen),
        SPELLS(Str.compendium_title_spells, ::SpellCompendiumScreen),
        BLESSINGS(Str.compendium_title_blessings, ::BlessingCompendiumScreen),
        MIRACLES(Str.compendium_title_miracles, ::MiracleCompendiumScreen),
        TRAITS(Str.compendium_title_traits, ::TraitCompendiumScreen),
        CAREERS(Str.compendium_title_careers, ::CareerCompendiumScreen),
    }

    @Composable
    protected fun <T : CompendiumItem<T>> ItemsList(
        liveItems: Flow<List<T>>,
        emptyUI: @Composable () -> Unit,
        onClick: (T) -> Unit,
        remover: suspend (T) -> Unit,
        newItemSaver: suspend (T) -> Unit,
        onNewItemRequest: () -> Unit,
        type: Type,
        itemContent: @Composable LazyItemScope.(T) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }

        SearchableList(
            data = liveItems.collectWithLifecycle(null).value
                ?.let { SearchableList.Data.Loaded(it) } ?: SearchableList.Data.Loading,
            emptyUi = emptyUI,
            title = type.localizedName,
            searchPlaceholder = "",
            searchableValue = { it.name },
            navigationIcon = { BackButton() },
            key = { it.id },
            itemContent = { item ->
                WithContextMenu(
                    items = listOf(
                        ContextMenu.Item(
                            stringResource(Str.common_ui_button_duplicate),
                            onClick = {
                                coroutineScope.launch { newItemSaver(item.duplicate()) }
                            }
                        ),
                        ContextMenu.Item(
                            stringResource(Str.common_ui_button_remove),
                            onClick = { coroutineScope.launch { remover(item) } }
                        ),
                    ),
                    onClick = { onClick(item) },
                ) {
                    itemContent(item)
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNewItemRequest) {
                    Icon(
                        Icons.Rounded.Add,
                        stringResource(Str.compendium_icon_add_compendium_item),
                    )
                }
            }
        )
    }
}
