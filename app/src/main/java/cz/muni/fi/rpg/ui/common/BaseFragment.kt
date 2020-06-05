package cz.muni.fi.rpg.ui.common

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    /**
     * Set title of parent activity's action bar
     */
    protected fun setTitle(title: String) {
        val activity = requireActivity()

        if (activity is AppCompatActivity) {
            activity.supportActionBar?.title = title
        }
    }

    /**
     * Set subtitle of parent activity's action bar
     * Note: This is not really optimal way to do this, but I did not found another one
     */
    protected fun setSubtitle(subtitle: String) {
        val activity = requireActivity()

        if (activity is AppCompatActivity) {
            activity.supportActionBar?.subtitle = subtitle
        }
    }
}
