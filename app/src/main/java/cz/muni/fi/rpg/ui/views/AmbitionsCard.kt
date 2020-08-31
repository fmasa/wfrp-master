package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.TextViewCompat
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Ambitions

class AmbitionsCard(context: Context, attrs: AttributeSet) : CardView(context, attrs) {
    init {
        inflate(context, R.layout.view_ambitions_card, this)

        val cardTitle = findViewById<TextView>(R.id.cardTitle)
        context.obtainStyledAttributes(attrs, R.styleable.AmbitionsCard).apply {
            getString(R.styleable.AmbitionsCard_title)?.let { name -> cardTitle.text = name }

            val titleDrawable = getResourceId(R.styleable.AmbitionsCard_titleDrawable, -1)

            if (titleDrawable != -1) {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    cardTitle,
                    titleDrawable,
                    0,
                    0,
                    0
                )

                TextViewCompat.setCompoundDrawableTintList(
                    cardTitle,
                    ColorStateList.valueOf(resources.getColor(R.color.colorText))
                )
            }

            recycle()
        }
    }

    fun setValue(ambitions: Ambitions) {
        val shortTermAmbition = findViewById<TextView>(R.id.shortTermAmbition)
        shortTermAmbition.text = ambitions.shortTerm
        val isShortTermBlank = ambitions.shortTerm.isBlank()

        setViewVisibility(findViewById<TextView>(R.id.shortTermAmbitionNone), isShortTermBlank)
        setViewVisibility(shortTermAmbition, !isShortTermBlank)

        val longTermAmbition = findViewById<TextView>(R.id.longTermAmbition)
        longTermAmbition.text = ambitions.longTerm
        val isLongTermBlank = ambitions.longTerm.isBlank()

        setViewVisibility(findViewById<TextView>(R.id.longTermAmbitionNone), isLongTermBlank)
        setViewVisibility(longTermAmbition, !isLongTermBlank)
    }

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}