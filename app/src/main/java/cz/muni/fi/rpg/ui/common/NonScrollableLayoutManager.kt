package cz.muni.fi.rpg.ui.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NonScrollableLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean = false
}