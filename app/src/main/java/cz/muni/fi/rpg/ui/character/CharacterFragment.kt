package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsFragment
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.StaticFragmentsViewPagerAdapter
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*


class CharacterFragment(
    private val adManager: AdManager
) : PartyScopedFragment(R.layout.fragment_character) {

    private val args: CharacterFragmentArgs by navArgs()
    private val viewModel: CharacterViewModel by viewModel { parametersOf(args.characterId) }
    private val auth: AuthenticationViewModel by viewModel { parametersOf(args.characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Created view for CharacterFragment (characterId = ${args.characterId})")

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
            it.map {
                character -> setTitle(character.getName())
                view.findViewById<View>(R.id.mainView).visibility = View.VISIBLE
                view.findViewById<View>(R.id.progress).visibility = View.GONE
            }
        }

        party.observe(viewLifecycleOwner) { setSubtitle(it.getName()) }


        val pager = view.findViewById<ViewPager2>(R.id.pager)
        pager.adapter = StaticFragmentsViewPagerAdapter(
            this,
            arrayOf(
                { CharacterMiscFragment.newInstance(args.characterId) },
                { CharacterStatsFragment.newInstance(args.characterId) },
                { CharacterSkillsFragment.newInstance(args.characterId) },
                { CharacterSpellsFragment.newInstance(args.characterId) },
                { InventoryFragment.newInstance(args.characterId) }
            )
        )

        TabLayoutMediator(view.findViewById(R.id.tabLayout), pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setText(R.string.title_misc)
                }
                1 -> {
                    tab.setText(R.string.title_character_stats)
                }
                2 -> {
                    tab.setText(R.string.title_character_skills)
                }
                3 -> {
                    tab.setText(R.string.title_character_spells)
                }
                4 -> {
                    tab.setText(R.string.title_character_trappings)
                }
            }
        }.attach()

        adManager.initializeUnit(view.findViewById(R.id.characterAdView))
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
}