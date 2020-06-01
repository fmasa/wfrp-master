package cz.muni.fi.rpg.ui.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Generic ViewPager adapter that can be used to create fragments using given factories
 */
class StaticFragmentsViewPagerAdapter(
    activity: FragmentActivity,
    private val fragmentFactories: Array<() -> Fragment>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = fragmentFactories.size

    override fun createFragment(position: Int) = fragmentFactories[position]()
}