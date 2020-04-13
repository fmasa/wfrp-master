package cz.muni.fi.rpg.ui.partyList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.ui.gameMaster.GameMasterActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.partyList.adapter.PartyHolder
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_party_list.*
import javax.inject.Inject

class PartyListActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var parties: PartyRepository

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = auth.currentUser;

        if (user == null) {
            startActivity(Intent(this, PartyListActivity::class.java))

            Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_LONG)
                .show()

            finish()

            return
        }

        setContentView(R.layout.activity_party_list)

        supportActionBar?.title = getString(R.string.party_activity_title)

        partyListRecycler.layoutManager = LinearLayoutManager(applicationContext);
        partyListRecycler.adapter = parties.forUser(
            user.uid,
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
            AssemblePartyDialog(user.uid, this::goToGameMasterActivity)
                .show(supportFragmentManager, "AssemblePartyDialog")
            fabMenu.collapse()
        }
    }

    private fun goToGameMasterActivity(party: Party) {
        val intent = Intent(this, GameMasterActivity::class.java);
        intent.putExtra(GameMasterActivity.EXTRA_PARTY_ID, party.id.toString())

        startActivity(intent)
    }
}
