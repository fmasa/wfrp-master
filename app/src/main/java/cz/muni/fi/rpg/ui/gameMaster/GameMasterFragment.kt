package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.time.DateTime
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.gameMaster.calendar.ChangeDateDialog
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncounterDialog
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncountersScreen
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

class GameMasterFragment(
    private val adManager: AdManager
) : PartyScopedFragment(0), TimePickerDialog.OnTimeSetListener {

    private val args: GameMasterFragmentArgs by navArgs()

    private lateinit var partyName: String

    private val viewModel: GameMasterViewModel by viewModel { parametersOf(args.partyId) }
    private val encountersViewModel: EncountersViewModel by viewModel { parametersOf(args.partyId) }

    override fun getPartyId(): UUID = args.partyId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                val party = viewModel.party.right().observeAsState().value
                WithConstraints(Modifier.fillMaxSize()) {
                    val screens = screens(Modifier.width(maxWidth).padding(top = 6.dp))
                    val screenWidth = constraints.maxWidth.toFloat()

                    Column(Modifier.fillMaxHeight()) {
                        val scrollState = rememberScrollState(0f, screenWidth)

                        TabRow(
                            screens,
                            scrollState = scrollState,
                            screenWidth = screenWidth,
                            fullWidthTabs = true,
                        )

                        if (party == null) {
                            Box(Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                                CircularProgressIndicator()
                            }
                            return@Column
                        }

                        TabContent(
                            item = party,
                            screens = screens,
                            scrollState = scrollState,
                            screenWidth = screenWidth,
                            modifier = Modifier.weight(1f)
                        )

                        BannerAd(
                            unitId = stringResource(R.string.game_master_ad_unit_id),
                            adManager = adManager
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        Timber.d("Created view for GameMasterFragment (partyId = ${args.partyId}")

        party.observe(viewLifecycleOwner) {
            setTitle(it.getName())
            partyName = it.getName()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionEdit) {
            RenamePartyDialog.newInstance(args.partyId, partyName).show(childFragmentManager, null)
        }

        return super.onOptionsItemSelected(item)
    }

    @Composable
    private fun screens(modifier: Modifier): Array<TabScreen<Party>> = arrayOf(
        TabScreen(R.string.title_characters) { party ->
            PartySummaryScreen(
                modifier = modifier,
                partyId = party.id,
                viewModel = viewModel,
                onCharacterOpenRequest = {
                    findNavController().navigate(
                        GameMasterFragmentDirections.openCharacter(CharacterId(getPartyId(), it.id))
                    )
                },
                onCharacterCreateRequest = {
                    findNavController().navigate(
                        GameMasterFragmentDirections.createCharacter(getPartyId(), it)
                    )
                },
                onInvitationDialogRequest = {
                    InvitationDialog.newInstance(it).show(childFragmentManager, null)
                },
                onEditAmbitionsRequest = { ambitions ->
                    ChangeAmbitionsDialog
                        .newInstance(getString(R.string.title_party_ambitions), ambitions)
                        .setOnSaveListener { viewModel.updatePartyAmbitions(it) }
                        .show(childFragmentManager, null)
                }
            )
        },
        TabScreen(R.string.title_calendar) { party ->
            CalendarScreen(
                party,
                modifier = modifier,
                onChangeTimeRequest = {
                    val time = party.getTime().time
                    TimePickerDialog.newInstance(
                        this@GameMasterFragment,
                        time.hour,
                        time.minute,
                        true
                    ).show(childFragmentManager, "TimePickerDialog")
                },
                onChangeDateRequest = {
                    ChangeDateDialog.newInstance(party.id, party.getTime().date)
                        .show(childFragmentManager, null)
                },
            )
        },
        TabScreen(R.string.title_encounters) { party ->
            EncountersScreen(
                encountersViewModel,
                modifier = modifier,
                onEncounterClick = {
                    findNavController().navigate(
                        GameMasterFragmentDirections.openEncounter(
                            EncounterId(partyId = party.id, encounterId = it.id)
                        )
                    )
                },
                onNewEncounterDialogRequest = {
                    EncounterDialog.newInstance(party.id, null).show(childFragmentManager, null)
                }
            )
        },
    )

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        viewModel.changeTime { it.withTime(DateTime.TimeOfDay(hourOfDay, minute)) }
    }
}