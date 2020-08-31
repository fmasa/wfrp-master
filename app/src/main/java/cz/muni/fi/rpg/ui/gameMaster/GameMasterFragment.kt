package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.StaticFragmentsViewPagerAdapter
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncountersFragment
import timber.log.Timber
import java.util.*

class GameMasterFragment(
    private val adManager: AdManager
) : PartyScopedFragment(R.layout.fragment_game_master) {

    private val args: GameMasterFragmentArgs by navArgs()

    private lateinit var partyName: String

    override fun getPartyId(): UUID = args.partyId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Created view for GameMasterFragment (partyId = ${args.partyId}")

        val mainView = view.findViewById<View>(R.id.mainView)
        val progress = view.findViewById<View>(R.id.progress)

        party.observe(viewLifecycleOwner) { party ->
            mainView.visibility = View.VISIBLE
            progress.visibility = View.GONE
            setTitle(party.getName())

            partyName = party.getName()
            setHasOptionsMenu(true)

            mainView.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }

        val pager = view.findViewById<ViewPager2>(R.id.pager)
        pager.adapter = StaticFragmentsViewPagerAdapter(
            this,
            arrayOf(
                { PartySummaryFragment.newInstance(args.partyId) },
                { CalendarFragment.newInstance(args.partyId) },
                { EncountersFragment.newInstance(args.partyId) }
            )
        )

        TabLayoutMediator(view.findViewById(R.id.tabLayout), pager) { tab, position ->
            tab.setText(
                when(position) {
                    0 -> R.string.title_characters
                    1 -> R.string.title_calendar
                    2 -> R.string.title_encounters
                    else -> error("Unknown tab on position: $position")
                }
            )
        }.attach()

        adManager.initializeUnit(view.findViewById(R.id.gameMasterAdView))
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
}