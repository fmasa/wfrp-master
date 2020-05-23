package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Money
import kotlinx.android.synthetic.main.view_money.view.*

class MoneyView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var _value: Money = Money.zero()

    var value: Money
        get() = _value
        set(newValue) {
            if (_value == newValue) return

            _value = newValue

            crowns.text = newValue.getCrowns().toString()
            shillings.text = newValue.getShillings().toString()
            pennies.text = newValue.getPennies().toString()
        }

    init {
        inflate(getContext(), R.layout.view_money, this)
    }
}
