package cz.muni.fi.rpg.ui.common

import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment

/*
 * Utility extension functions for UI components
 */

fun View.toggleVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun Fragment.stringArgument(name: String) = lazy {
    requireArguments().getString(name) ?: error("Argument $name was not passed")
}

inline fun <reified T : Parcelable> Fragment.parcelableArgument(name: String) = lazy {
    requireArguments().getParcelable<T>(name) ?: error("Argument $name was not passed")
}

inline fun <reified T : Parcelable> Fragment.optionalParcelableArgument(name: String) = lazy {
    requireArguments().getParcelable<T>(name)
}