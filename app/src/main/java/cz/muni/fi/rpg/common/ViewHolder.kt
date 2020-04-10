package cz.muni.fi.rpg.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<T>(v: View) : RecyclerView.ViewHolder(v) {
    abstract fun bind(item: T, onClickListener: OnClickListener<T>)
}