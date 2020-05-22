package cz.muni.fi.rpg.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.dialog_about.view.*

class AboutDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_about, null)

        view.appVersion.text = BuildConfig.VERSION_NAME
        return AlertDialog.Builder(context)
            .setView(view)
            .create()
    }
}