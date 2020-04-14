package cz.muni.fi.rpg.ui.partyList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.ui.gameMaster.GameMasterActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.AuthenticatedActivity
import cz.muni.fi.rpg.ui.JoinPartyActivity
import cz.muni.fi.rpg.ui.partyList.adapter.PartyHolder
import kotlinx.android.synthetic.main.activity_party_list.*
import javax.inject.Inject

class PartyListActivity : AuthenticatedActivity(R.layout.activity_party_list) {
    @Inject
    lateinit var parties: PartyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = getString(R.string.party_activity_title)

        partyListRecycler.layoutManager = LinearLayoutManager(applicationContext);
        partyListRecycler.adapter = parties.forUser(
            getUserId(),
            {
                PartyHolder(
                    LayoutInflater.from(it.context).inflate(
                        R.layout.party_item,
                        it,
                        false
                    )
                )
            },
            this::goToGameMasterActivity
        )

        assembleNewParty.setOnClickListener {
            AssemblePartyDialog(getUserId(), this::goToGameMasterActivity)
                .show(supportFragmentManager, "AssemblePartyDialog")
            fabMenu.collapse()
        }

        scanQrCode.setOnClickListener {
            startActivity(Intent(this, JoinPartyActivity::class.java))
            fabMenu.collapse()
        }
    }

    private fun goToGameMasterActivity(party: Party) {
        val intent = Intent(this, GameMasterActivity::class.java);
        intent.putExtra(GameMasterActivity.EXTRA_PARTY_ID, party.id.toString())

        startActivity(intent)
    }
}
