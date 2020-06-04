package cz.muni.fi.rpg.ui.partyList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.ui.gameMaster.GameMasterActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.AuthenticatedActivity
import cz.muni.fi.rpg.ui.character.CharacterActivity
import cz.muni.fi.rpg.ui.joinParty.JoinPartyActivity
import cz.muni.fi.rpg.ui.partyList.adapter.PartyAdapter
import kotlinx.android.synthetic.main.activity_party_list.*
import org.koin.android.ext.android.inject

class PartyListActivity : AuthenticatedActivity(R.layout.activity_party_list) {

    private val parties: PartyRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyListRecycler.layoutManager = LinearLayoutManager(applicationContext)

        val adapter = PartyAdapter(layoutInflater) {
            if (it.gameMasterId == getUserId()) {
                GameMasterActivity.start(it.id, this)
            } else {
                CharacterActivity.start(CharacterId(it.id, getUserId()), this)
            }
        }
        partyListRecycler.adapter = adapter

        parties.forUser(getUserId()).observe(this) {
            adapter.submitList(it)

            if (it.isNotEmpty()) {
                noPartiesIcon.visibility = View.GONE
                noPartiesText.visibility = View.GONE
                partyListRecycler.visibility = View.VISIBLE
            } else {
                noPartiesIcon.visibility = View.VISIBLE
                noPartiesText.visibility = View.VISIBLE
                partyListRecycler.visibility = View.GONE
            }
        }

        assembleNewParty.setOnClickListener {
            AssemblePartyDialog(
                getUserId(),
                { party -> GameMasterActivity.start(party.id, this) },
                parties
            ).show(supportFragmentManager, "AssemblePartyDialog")
            fabMenu.collapse()
        }

        scanQrCode.setOnClickListener {
            startActivity(Intent(this, JoinPartyActivity::class.java))
            fabMenu.collapse()
        }
    }
}
