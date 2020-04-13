package cz.muni.fi.rpg

import android.os.Bundle
import com.google.gson.Gson
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_game_master.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class GameMasterActivity : DaggerAppCompatActivity(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val EXTRA_PARTY_ID = "partyId"
    }

    private lateinit var partyId: UUID

    @Inject
    lateinit var parties: PartyRepository

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyId = intent.getStringExtra(EXTRA_PARTY_ID)?.let { UUID.fromString(it) }
            ?: throw IllegalAccessException("'${EXTRA_PARTY_ID}' must be provided")

        setContentView(R.layout.activity_game_master)

        launch {
            val party = parties.get(partyId)

            withContext(Dispatchers.Main) { supportActionBar?.title = party.name }
            partyInviteQrCode.drawCode(gson.toJson(party.getInvitation()))
        }
    }
}
