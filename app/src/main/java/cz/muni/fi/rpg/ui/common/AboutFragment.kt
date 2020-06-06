package cz.muni.fi.rpg.ui.common

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.fragment_about.view.*

class AboutFragment : BaseFragment(R.layout.fragment_about) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.appVersion.text = BuildConfig.VERSION_NAME
    }
}