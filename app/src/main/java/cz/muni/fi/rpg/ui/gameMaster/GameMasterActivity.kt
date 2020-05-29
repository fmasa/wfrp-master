package cz.muni.fi.rpg.ui.gameMaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.R
import android.view.View
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.character.CharacterActivity
import cz.muni.fi.rpg.ui.gameMaster.adapter.CharacterAdapter
import kotlinx.android.synthetic.main.activity_game_master.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class GameMasterActivity : PartyScopedActivity(R.layout.activity_game_master),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        fun start(partyId: UUID, packageContext: Context) {
            val intent = Intent(packageContext, GameMasterActivity::class.java);
            intent.putExtra(EXTRA_PARTY_ID, partyId.toString())

            packageContext.startActivity(intent)
        }
    }

    @Inject
    lateinit var jsonMapper: JsonMapper
    @Inject
    lateinit var characterRepo: CharacterRepository

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setEmptyCollectionView(isEmpty: Boolean) {
        setViewVisibility(noCharactersIcon, isEmpty)
        setViewVisibility(noCharactersText, isEmpty)
        setViewVisibility(characterListRecycler, !isEmpty)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyViewModel.party.right().observe(this) { party ->
            supportActionBar?.title = party.name
            launch { partyInviteQrCode.drawCode(jsonMapper.writeValueAsString(party.getInvitation())) }
        }

        characterRepo.inParty(getPartyId()).observe(this) { characters ->
            if (characters.isNotEmpty()) {
                val adapter = CharacterAdapter(layoutInflater)
                {
                    val intent = Intent(this, CharacterActivity::class.java);
                    intent.putExtra(PartyScopedActivity.EXTRA_PARTY_ID, getPartyId().toString())
                    intent.putExtra(CharacterActivity.EXTRA_CHARACTER_ID, it.userId)
                    startActivity(intent)
                }
                characterListRecycler.adapter = adapter
                characterListRecycler.layoutManager = LinearLayoutManager(applicationContext)

                adapter.submitList(characters)

                setEmptyCollectionView(false)
            } else {
                setEmptyCollectionView(true)
            }
        }
    }
}
