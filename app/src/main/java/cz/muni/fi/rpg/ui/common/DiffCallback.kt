package cz.muni.fi.rpg.ui.common

import androidx.recyclerview.widget.DiffUtil

class DiffCallback<T>(
    private val areItemsTheSameCallback: (T, T) -> Boolean,
    private val areContentsTheSameCallback: (T, T) -> Boolean,
) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(a: T, b: T) = areItemsTheSameCallback(a, b)

    override fun areContentsTheSame(a: T, b: T) = areContentsTheSameCallback(a, b)
}