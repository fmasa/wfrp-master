package cz.muni.fi.rpg.partyList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.GameMasterActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.Party
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import cz.muni.fi.rpg.partyList.adapter.PartyHolder
import kotlinx.android.synthetic.main.activity_party_list.*

class PartyListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parties = FirestorePartyRepository();

        val auth = FirebaseAuth.getInstance();

        val user = auth.currentUser;

        if (user == null) {
            startActivity(Intent(this, PartyListActivity::class.java))

            Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_LONG)
                .show()

            finish()

            return
        }

        setContentView(R.layout.activity_party_list)

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
