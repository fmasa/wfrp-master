package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsScreen
import cz.muni.fi.rpg.ui.character.skills.SkillDialog
import cz.muni.fi.rpg.ui.character.skills.TalentDialog
import cz.muni.fi.rpg.ui.character.spells.SpellDialog
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.composables.BannerAd
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.common.composables.animatedTabIndicatorOffset
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


class CharacterFragment(
    private val adManager: AdManager
) : PartyScopedFragment(0),
    CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val args: CharacterFragmentArgs by navArgs()
    private val viewModel: CharacterViewModel by viewModel { parametersOf(args.characterId) }
    private val auth: AuthenticationViewModel by viewModel { parametersOf(args.characterId) }

    private val characteristicsVm: CharacterStatsViewModel by viewModel { parametersOf(args.characterId) }
    private val miscVm: CharacterMiscViewModel by viewModel { parametersOf(args.characterId) }
    private val skillsVm: SkillsViewModel by viewModel { parametersOf(args.characterId) }
    private val talentsVm: TalentsViewModel by viewModel { parametersOf(args.characterId) }
    private val spellsVm: SpellsViewModel by viewModel { parametersOf(args.characterId) }
    private val inventoryVm: InventoryViewModel by viewModel { parametersOf(args.characterId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                WithConstraints(Modifier.fillMaxSize()) {
                    val screenWidth = constraints.maxWidth.toFloat()
                    val screens = screens(Modifier.width(maxWidth).padding(top = 6.dp))

                    Column(Modifier.fillMaxHeight()) {
                        val scrollState = rememberScrollState()

                        Tabs(screens, scrollState, screenWidth)

                        val character = viewModel.character.right().observeAsState().value

                        if (character == null) {
                            Box(Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                                CircularProgressIndicator()
                            }
                            return@Column
                        }

                        val canDrag = mutableStateOf(true)

                        Row(
                            modifier = Modifier
                                .clipToBounds()
                                .padding(0.dp)
                                .horizontalScroll(scrollState, false)
                                .weight(1f)
                                .background(MaterialTheme.colors.background)
                                .draggable(
                                    Orientation.Horizontal,
                                    reverseDirection = true,
                                    onDrag = { scrollState.scrollBy(it) },
                                    onDragStopped = {
                                        val offset = scrollState.value
                                        val startOffset = offset - offset % screenWidth
                                        val endOffset =
                                            screenWidth * (offset / screenWidth).roundToInt()
                                        canDrag.value = false
                                        scrollState.smoothScrollTo(
                                            if (abs(startOffset - offset) < abs(endOffset - offset))
                                                startOffset
                                            else endOffset,
                                            onEnd = { _, _ -> canDrag.value = true },
                                        )
                                    }
                                ),

                            horizontalArrangement = Arrangement.Start,
                            verticalGravity = Alignment.Top
                        ) {
                            screens.forEach { it.content(character) }
                        }

                        BannerAd(stringResource(R.string.character_ad_unit_id), adManager)
                    }
                }

            }
        }
    }

    private fun openExperiencePointsDialog(currentXpPoints: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_xp, null, false)

        val xpPointsInput = view.findViewById<TextInput>(R.id.xpPointsInput)
        xpPointsInput.setDefaultValue(currentXpPoints.toString())

        AlertDialog.Builder(requireContext(), R.style.FormDialog)
            .setTitle("Change amount of XP")
            .setView(view)
            .setPositiveButton(R.string.button_save) { _, _ ->
                val xpPoints = xpPointsInput.getValue().toIntOrNull() ?: 0
                launch { miscVm.updateExperiencePoints(xpPoints) }
            }.create()
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel.character.observe(viewLifecycleOwner) {
            it.mapLeft { e ->
                Timber.e(e, "Character not found")

                if (args.characterId.isDerivedFromUserId(auth.getUserId())) {
                    openCharacterCreation(auth.getUserId())
                } else {
                    findNavController().popBackStack(R.id.nav_party_list, false)
                }
            }
            it.map { character -> setTitle(character.getName()) }
        }

        party.observe(viewLifecycleOwner) { setSubtitle(it.getName()) }
    }

    override fun getPartyId(): UUID = args.characterId.partyId

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionEdit) {
            findNavController()
                .navigate(
                    CharacterFragmentDirections.editCharacter(args.characterId)
                )
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openCharacterCreation(userId: String) {
        findNavController()
            .navigate(CharacterFragmentDirections.createCharacter(args.characterId.partyId, userId))
    }

    private fun openTalentDialog(existingTalent: Talent?) {
        val dialog = TalentDialog.newInstance(existingTalent)
        dialog.setOnSuccessListener { talent ->
            launch {
                talentsVm.saveTalent(talent)

                withContext(Dispatchers.Main) { dialog.dismiss() }
            }
        }

        dialog.show(childFragmentManager, "TalentDialog")
    }

    private fun openSkillDialog(existingSkill: Skill?) {
        SkillDialog.newInstance(args.characterId, existingSkill).show(childFragmentManager, null)
    }

    @Composable
    private fun screens(modifier: Modifier): Array<CharacterScreen> {
        val fragmentManager = childFragmentManager
        val characterId = args.characterId

        return arrayOf(
            CharacterScreen(
                R.string.title_misc
            ) { character ->
                CharacterMiscScreen(
                    modifier = modifier,
                    character = character,
                    viewModel = miscVm,
                    onXpButtonClick = ::openExperiencePointsDialog,
                    onCharacterAmbitionsClick = { defaults ->
                        ChangeAmbitionsDialog
                            .newInstance(getString(R.string.title_character_ambitions), defaults)
                            .setOnSaveListener { miscVm.updateCharacterAmbitions(it) }
                            .show(fragmentManager, "ChangeAmbitionsDialog")
                    },
                )
            },
            CharacterScreen(R.string.title_character_stats) { character ->
                CharacterCharacteristicsScreen(
                    character = character,
                    viewModel = characteristicsVm,
                    modifier = modifier,
                )
            },
            CharacterScreen(R.string.title_character_skills) {
                CharacterSkillsScreen(
                    talentsVm = talentsVm,
                    skillsVm = skillsVm,
                    characterVm = viewModel,
                    modifier = modifier,
                    onTalentDialogRequest = ::openTalentDialog,
                    onSkillDialogRequest = ::openSkillDialog
                )
            },
            CharacterScreen(R.string.title_character_spells) {
                CharacterSpellsScreen(
                    viewModel = spellsVm,
                    modifier = modifier,
                    onSpellDialogRequest = {
                        SpellDialog.newInstance(characterId, it).show(fragmentManager, null)
                    }
                )
            },
            CharacterScreen(R.string.title_character_trappings) {
                CharacterTrappingsScreen(
                    viewModel = inventoryVm,
                    modifier = modifier,
                    onItemDialogRequest = {
                        InventoryItemDialog.newInstance(characterId, it).show(fragmentManager, null)
                    },
                    onMoneyDialogRequest = {
                        TransactionDialog.newInstance(characterId).show(fragmentManager, null)
                    }
                )
            },
        )
    }
}

@Composable
private fun Tabs(screens: Array<CharacterScreen>, scrollState: ScrollState, screenWidth: Float) {
    val selectedTabIndex = (scrollState.value / screenWidth).toInt()
    val scrolledPercentage = ((scrollState.value % screenWidth) / screenWidth)

    ScrollableTabRow(
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = if (scrolledPercentage > 0.5f) selectedTabIndex + 1 else selectedTabIndex,
        backgroundColor = MaterialTheme.colors.surface,
        indicator = { tabPositions ->
            TabConstants.DefaultIndicator(
                Modifier.animatedTabIndicatorOffset(
                    tabPositions,
                    selectedTabIndex,
                    scrolledPercentage
                ),
                color = MaterialTheme.colors.primary
            )
        }
    ) {
        screens.forEachIndexed { index, screen ->
            Tab(
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = EmphasisAmbient.current.medium.applyEmphasis(
                    MaterialTheme.colors.onSurface
                ),
                selected = index == (if (scrolledPercentage > 0.5f) selectedTabIndex + 1 else selectedTabIndex),
                onClick = { scrollState.smoothScrollTo(index * screenWidth) },
                text = { Text(stringResource(screen.tabName).toUpperCase(Locale.getDefault())) },
            )
        }
    }
}

data class CharacterScreen(
    @StringRes internal val tabName: Int,
    internal val content: @Composable (Character) -> Unit
)
