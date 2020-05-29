package cz.muni.fi.rpg.ui.gameMaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.R
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.character.CharacterActivity
import cz.muni.fi.rpg.ui.gameMaster.adapter.CharacterAdapter
import cz.muni.fi.rpg.ui.views.QrCode
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

    private lateinit var invitation: Invitation

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
            inviteButton.isEnabled = true
            invitation = party.getInvitation()
        }

        inviteButton.setOnClickListener { showQrCode() }

        characterRepo.inParty(getPartyId()).observe(this) { characters ->
            if (characters.isNotEmpty()) {
                val adapter = CharacterAdapter(layoutInflater)
                { CharacterActivity.start(CharacterId(getPartyId(), it.userId), this) }
                characterListRecycler.adapter = adapter
                characterListRecycler.layoutManager = LinearLayoutManager(applicationContext)

                adapter.submitList(characters)

                setEmptyCollectionView(false)
            } else {
                setEmptyCollectionView(true)
            }
        }
    }

    private fun showQrCode() {
        QrCodeDialog(invitation, jsonMapper)
            .show(supportFragmentManager, QrCodeDialog::class.simpleName)
    }
}
