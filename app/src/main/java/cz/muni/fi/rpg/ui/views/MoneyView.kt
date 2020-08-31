package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Money

class MoneyView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var value: Money = Money.zero()

    fun setValue(value: Money) {
        if (value == this.value) return

        this.value = value

        findViewById<TextView>(R.id.crowns).text = value.getCrowns().toString()
        findViewById<TextView>(R.id.shillings).text = value.getShillings().toString()
        findViewById<TextView>(R.id.pennies).text = value.getPennies().toString()
    }

    init {
        inflate(getContext(), R.layout.view_money, this)
    }
}
