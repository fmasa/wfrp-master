package cz.muni.fi.rpg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class GameMasterActivity : AppCompatActivity() {
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
