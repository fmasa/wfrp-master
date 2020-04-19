package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import com.google.gson.Gson
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.android.synthetic.main.activity_game_master.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameMasterActivity: PartyScopedActivity(R.layout.activity_game_master),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launch {
            val party = parties.get(getPartyId())

            withContext(Dispatchers.Main) { supportActionBar?.title = party.name }
            partyInviteQrCode.drawCode(gson.toJson(party.getInvitation()))
        }
    }
}
