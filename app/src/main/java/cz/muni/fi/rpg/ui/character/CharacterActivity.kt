package cz.muni.fi.rpg.ui.character

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.observe
import com.google.android.material.tabs.TabLayoutMediator
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationActivity
import cz.muni.fi.rpg.ui.common.StaticFragmentsViewPagerAdapter
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.activity_character.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterActivity : PartyScopedActivity(R.layout.activity_character) {
    companion object {
        private const val EXTRA_CHARACTER_ID = "characterId";

        fun start(characterId: CharacterId, packageContext: Context) {
            val intent = Intent(packageContext, CharacterActivity::class.java);
            intent.putExtra(EXTRA_PARTY_ID, characterId.partyId.toString())
            intent.putExtra(EXTRA_CHARACTER_ID, characterId.userId)

            packageContext.startActivity(intent)
        }
    }

    private val characterId by lazy {
        intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: throw IllegalAccessException("'${EXTRA_CHARACTER_ID}' must be provided")
    }

    private val viewModel: CharacterViewModel by viewModel {
        parametersOf(CharacterId(getPartyId(), characterId))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.character.observe(this) {
            it.mapLeft { openCharacterCreation() }
            it.map { character -> supportActionBar?.title = character.getName() }
        }

        partyViewModel.party
            .right()
            .observe(this) { supportActionBar?.subtitle = it.name }

        pager.adapter = StaticFragmentsViewPagerAdapter(
            this,
            arrayOf(
                { CharacterStatsFragment() },
                { CharacterSkillsFragment() },
//                { CharacterSpellsFragment() },
                { InventoryFragment() }
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
//                2 -> {
//                    tab.setText(R.string.title_character_spells)
//                    tab.setIcon(R.drawable.ic_spells)
//                }
                2 -> {
                    tab.setText(R.string.title_character_inventory)
                    tab.setIcon(R.drawable.ic_inventory)
                }
            }
        }.attach()
    }

    private fun openCharacterCreation() {
        Log.e(localClassName, "Character not found");

        val intent = Intent(this, CharacterCreationActivity::class.java)
        intent.putExtra(EXTRA_PARTY_ID, getPartyId().toString())
        intent.putExtra(CharacterCreationActivity.EXTRA_CHARACTER_ID, characterId)


        startActivity(intent)
        finish()
    }
}