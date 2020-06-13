package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Ambitions
import kotlinx.android.synthetic.main.view_ambitions_card.view.*

class AmbitionsCard(context: Context, attrs: AttributeSet) : CardView(context, attrs) {
    init {
        inflate(context, R.layout.view_ambitions_card, this)
        context.obtainStyledAttributes(attrs, R.styleable.AmbitionsCard).apply {
            getString(R.styleable.AmbitionsCard_title)?.let { name -> cardTitle.text = name }
            recycle()
        }
    }

    fun setValue(ambitions: Ambitions) {
        shortTermAmbition.text = ambitions.shortTerm
        val isShortTermBlank = ambitions.shortTerm.isBlank()

        setViewVisibility(shortTermAmbitionNone, isShortTermBlank)
        setViewVisibility(shortTermAmbition, !isShortTermBlank)

        longTermAmbition.text = ambitions.longTerm
        val isLongTermBlank = ambitions.longTerm.isBlank()

        setViewVisibility(longTermAmbitionNone, isLongTermBlank)
        setViewVisibility(longTermAmbition, !isLongTermBlank)
    }

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}