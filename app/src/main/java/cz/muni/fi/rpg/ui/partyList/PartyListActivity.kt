package cz.muni.fi.rpg.ui.partyList

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.ui.gameMaster.GameMasterActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.AuthenticatedActivity
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.character.CharacterActivity
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationActivity
import cz.muni.fi.rpg.ui.joinParty.JoinPartyActivity
import cz.muni.fi.rpg.ui.partyList.adapter.PartyHolder
import kotlinx.android.synthetic.main.activity_party_list.*
import javax.inject.Inject
import kotlin.reflect.KClass

class PartyListActivity : AuthenticatedActivity(R.layout.activity_party_list) {
    @Inject
    lateinit var parties: PartyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyListRecycler.layoutManager = LinearLayoutManager(applicationContext)
        partyListRecycler.adapter = parties.forUser(getUserId(), this) { group ->
            PartyHolder(
                layoutInflater.inflate(R.layout.party_item, group, false)
            ) {
                if (it.gameMasterId == getUserId()) {
                    openParty(it, GameMasterActivity::class)
                } else {
                    openParty(it, CharacterActivity::class)
                }
            }
        }

        assembleNewParty.setOnClickListener {
            AssemblePartyDialog(getUserId()) { openParty(it, GameMasterActivity::class) }
                .show(supportFragmentManager, "AssemblePartyDialog")
            fabMenu.collapse()
        }

        scanQrCode.setOnClickListener {
            startActivity(Intent(this, JoinPartyActivity::class.java))
            fabMenu.collapse()
        }
    }

    private fun <T : PartyScopedActivity> openParty(party: Party, activityClass: KClass<T>) {
        val intent = Intent(this, activityClass.java);
        intent.putExtra(PartyScopedActivity.EXTRA_PARTY_ID, party.id.toString())

        startActivity(intent)
    }
}
