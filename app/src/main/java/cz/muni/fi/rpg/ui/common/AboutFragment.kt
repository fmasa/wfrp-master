package cz.muni.fi.rpg.ui.common

import android.os.Bundle
import android.view.View
import android.widget.TextView
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.R

class AboutFragment : BaseFragment(R.layout.fragment_about) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.appVersion).text = BuildConfig.VERSION_NAME
    }
}