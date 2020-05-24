package cz.muni.fi.rpg.ui.gameMaster

import android.os.Bundle
import androidx.lifecycle.observe
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.android.synthetic.main.activity_game_master.*
import kotlinx.coroutines.*
import javax.inject.Inject

class GameMasterActivity : PartyScopedActivity(R.layout.activity_game_master),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    @Inject
    lateinit var jsonMapper: JsonMapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyViewModel.party.right().observe(this) {party ->
            supportActionBar?.title = party.name
            launch { partyInviteQrCode.drawCode(jsonMapper.writeValueAsString(party.getInvitation())) }
        }
    }
}
