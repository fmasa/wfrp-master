package cz.muni.fi.rpg

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import java.util.*

class GameMasterActivity : DaggerAppCompatActivity() {
    companion object {
        const val EXTRA_PARTY_ID = "partyId"
    }

    private lateinit var partyId: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyId = intent.getStringExtra(EXTRA_PARTY_ID)?.let { UUID.fromString(it) }
            ?: throw IllegalAccessException("'${EXTRA_PARTY_ID}' must be provided");

        setContentView(R.layout.activity_game_master)
    }
}
