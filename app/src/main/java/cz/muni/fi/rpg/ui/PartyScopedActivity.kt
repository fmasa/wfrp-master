package cz.muni.fi.rpg.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import cz.muni.fi.rpg.viewModels.PartyViewModel
import cz.muni.fi.rpg.viewModels.PartyViewModelProvider
import java.util.*
import javax.inject.Inject

abstract class PartyScopedActivity(@LayoutRes contentLayoutId: Int) :
    AuthenticatedActivity(contentLayoutId){
    companion object {
        const val EXTRA_PARTY_ID = "partyId"
    }

    @Inject
    lateinit var partyViewModelProvider: PartyViewModelProvider

    protected val partyViewModel: PartyViewModel by viewModels {
        partyViewModelProvider.provide(getPartyId())
    }

    private lateinit var partyId: UUID

    protected fun getPartyId(): UUID = partyId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyId = intent.getStringExtra(EXTRA_PARTY_ID)?.let(UUID::fromString)
            ?: throw IllegalAccessException("'${EXTRA_PARTY_ID}' must be provided")
    }
}