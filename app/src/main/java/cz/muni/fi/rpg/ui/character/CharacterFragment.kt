package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsFragment
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.StaticFragmentsViewPagerAdapter
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_character.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class CharacterFragment(
    private val adManager: AdManager
) : BaseFragment(R.layout.fragment_character) {

    private val args: CharacterFragmentArgs by navArgs()
    private val viewModel: CharacterViewModel by viewModel { parametersOf(args.characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Created view for CharacterFragment (characterId = ${args.characterId})")

        setHasOptionsMenu(true)

        viewModel.character.observe(viewLifecycleOwner) {
            it.mapLeft { e -> openCharacterCreation(e) }
            it.map {
                character -> setTitle(character.getName())
                mainView.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        }

        viewModel.party.right()
            .observe(viewLifecycleOwner) { setSubtitle(it.getName()) }

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

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setText(R.string.title_misc)
                    tab.setIcon(R.drawable.ic_info)
                }
                1 -> {
                    tab.setText(R.string.title_character_stats)
                    tab.setIcon(R.drawable.ic_character)
                }
                2 -> {
                    tab.setText(R.string.title_character_skills)
                    tab.setIcon(R.drawable.ic_skills)
                }
                3 -> {
                    tab.setText(R.string.title_character_spells)
                    tab.setIcon(R.drawable.ic_spells)
                }
                4 -> {
                    tab.setText(R.string.title_character_trappings)
                    tab.setIcon(R.drawable.ic_inventory)
                }
            }
        }.attach()

        adManager.initializeUnit(characterAdView)
    }

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

    private fun openCharacterCreation(e: CharacterNotFound) {
        Timber.e(e, "Character not found")

        findNavController()
            .navigate(CharacterFragmentDirections.createCharacter(args.characterId.partyId))
    }
}