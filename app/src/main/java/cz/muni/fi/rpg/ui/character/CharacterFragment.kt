package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsFragment
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.StaticFragmentsViewPagerAdapter
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.PartyViewModel
import kotlinx.android.synthetic.main.fragment_character.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterFragment : BaseFragment(R.layout.fragment_character) {
    private val args: CharacterFragmentArgs by navArgs()

    private val viewModel: CharacterViewModel by viewModel { parametersOf(args.characterId) }
    private val partyVm: PartyViewModel by viewModel { parametersOf(args.characterId.partyId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel.character.observe(viewLifecycleOwner) {
            it.mapLeft { openCharacterCreation() }
            it.map { character -> setTitle(character.getName()) }
        }

        partyVm.party.right()
            .observe(viewLifecycleOwner) { setSubtitle(it.name) }

        pager.adapter = StaticFragmentsViewPagerAdapter(
            this,
            arrayOf(
                { CharacterStatsFragment.newInstance(args.characterId) },
                { CharacterSkillsFragment.newInstance(args.characterId) },
                { CharacterSpellsFragment.newInstance(args.characterId) },
                { InventoryFragment.newInstance(args.characterId) },
                { CharacterMiscFragment.newInstance(args.characterId) }
            )
        )

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setText(R.string.title_character_stats)
                    tab.setIcon(R.drawable.ic_character)
                }
                1 -> {
                    tab.setText(R.string.title_character_skills)
                    tab.setIcon(R.drawable.ic_skills)
                }
                2 -> {
                    tab.setText(R.string.title_character_spells)
                    tab.setIcon(R.drawable.ic_spells)
                }
                3 -> {
                    tab.setText(R.string.title_character_inventory)
                    tab.setIcon(R.drawable.ic_inventory)
                }
                4 -> {
                    tab.setText(R.string.title_misc)
                    tab.setIcon(R.drawable.ic_info)
                }
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.character_menu, menu)
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

    private fun openCharacterCreation() {
        Log.e("CharacterFragment", "Character not found")

        findNavController()
            .navigate(CharacterFragmentDirections.createCharacter(args.characterId.partyId))
    }
}