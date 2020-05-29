package cz.muni.fi.rpg.ui.common.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.annotation.LayoutRes

/**
 * As this (rather horrendous) class name suggests. This adapter makes sure that spinners width
 * is adjusted to contain currently selected item.
 *
 * So if we have for example items:
 * - Foo
 * - VeryLongText
 *
 * spinner won't add a lot of right whitespace to accommodate for "VeryLongText", when "Foo" is selected.
 * This is useful mostly when using spinner in the middle of text sentence.
 */
class SpinnerAdapterWithWidthMatchingSelectedItem(
    private val inner: SpinnerAdapter,
    @LayoutRes
    private val mainItemLayoutId: Int,
    private val layoutInflater: LayoutInflater
) : SpinnerAdapter by inner {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        require(parent is Spinner) { "This Adapter can be used only with spinner" }
        return inner.getView(
            parent.selectedItemPosition,
            convertView ?: layoutInflater.inflate(mainItemLayoutId, null),
            parent
        )
    }
}