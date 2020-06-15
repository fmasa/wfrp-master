package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Money
import kotlinx.android.synthetic.main.view_money.view.*

class MoneyView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var value: Money = Money.zero()

    fun setValue(value: Money) {
        if (value == this.value) return

        this.value = value

        crowns.text = value.getCrowns().toString()
        shillings.text = value.getShillings().toString()
        pennies.text = value.getPennies().toString()
    }

    init {
        inflate(getContext(), R.layout.view_money, this)
    }
}
