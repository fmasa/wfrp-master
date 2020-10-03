package cz.muni.fi.rpg.ui.common

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    private var actionBarVisibility: Boolean = true

    override fun onResume() {
        super.onResume()

        activity?.let { activity ->
            if (activity !is AppCompatActivity) {
                return
            }

            activity.supportActionBar?.let {
                when (actionBarVisibility) {
                    true -> it.show()
                    false -> it.hide()
                }
            }
        }
    }

    /**
     * Set title of parent activity's action bar
     */
    protected fun setTitle(title: String) {
        activity?.let { (activity as AppCompatActivity).supportActionBar?.title = title }
    }

    /**
     * Set subtitle of parent activity's action bar
     * Note: This is not really optimal way to do this, but I did not found another one
     */
    protected fun setSubtitle(subtitle: String) {
        activity?.let { (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle }
    }

    /**
     * This has to be called before onCreate to take effect
     */
    protected fun hideActionBar() {
        actionBarVisibility = false
    }
}
