package cz.muni.fi.rpg.ui.common

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.viewModels.PartyViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

abstract class PartyScopedFragment(@LayoutRes contentLayoutId: Int) :
    BaseFragment(contentLayoutId) {

    private val viewModel: PartyViewModel by viewModel { parametersOf(getPartyId()) }

    protected val party: LiveData<Party> by lazy { viewModel.party.right() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.party.observe(viewLifecycleOwner) {
            if (!isAvailable(it)) {
                Timber.d("Party does not exist or is archived: $it")
                toast(R.string.error_party_not_found, Toast.LENGTH_LONG)
                findNavController().popBackStack(R.id.nav_party_list, false)
            }
        }
    }

    protected abstract fun getPartyId(): UUID

    private fun isAvailable(partyOrError: Either<PartyNotFound, Party>): Boolean {
        return partyOrError.fold({ false }, { !it.isArchived() })
    }
}