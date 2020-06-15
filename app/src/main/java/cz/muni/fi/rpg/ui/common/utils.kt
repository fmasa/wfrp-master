package cz.muni.fi.rpg.ui.common

import android.view.View

/*
 * Utility extension functions for UI components
 */

fun View.toggleVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
