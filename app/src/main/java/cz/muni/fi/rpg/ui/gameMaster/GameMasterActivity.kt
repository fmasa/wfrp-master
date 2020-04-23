package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import androidx.lifecycle.observe
import com.google.gson.Gson
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.android.synthetic.main.activity_game_master.*
import kotlinx.coroutines.*
import javax.inject.Inject

class GameMasterActivity : PartyScopedActivity(R.layout.activity_game_master),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    @Inject
    lateinit var gson: Gson

    private val party by lazy { parties.getLive(getPartyId()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        party.observe(this) {
            it.map { party ->
                supportActionBar?.title = party.name
                async { partyInviteQrCode.drawCode(gson.toJson(party.getInvitation())) }
            }
        }
    }
}
