package cz.muni.fi.rpg.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import cz.muni.fi.rpg.viewModels.PartyViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

abstract class PartyScopedActivity(@LayoutRes contentLayoutId: Int) :
    AuthenticatedActivity(contentLayoutId){
    companion object {
        const val EXTRA_PARTY_ID = "partyId"
    }

    protected val partyViewModel: PartyViewModel by viewModel { parametersOf(getPartyId()) }

    private lateinit var partyId: UUID

    protected fun getPartyId(): UUID = partyId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partyId = intent.getStringExtra(EXTRA_PARTY_ID)?.let(UUID::fromString)
            ?: throw IllegalAccessException("'${EXTRA_PARTY_ID}' must be provided")
    }
}