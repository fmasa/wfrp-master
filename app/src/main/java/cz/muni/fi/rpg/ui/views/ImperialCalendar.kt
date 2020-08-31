package cz.muni.fi.rpg.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.time.ImperialDate
import cz.muni.fi.rpg.ui.common.ColorCircleDrawable
import cz.muni.fi.rpg.ui.common.toggleVisibility
import timber.log.Timber

class ImperialCalendar(date: ImperialDate, context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    constructor(context: Context, attrs: AttributeSet?) : this(defaultDate, context, attrs)
    constructor(date: ImperialDate, context: Context) : this(date, context, null)

    companion object {
        private val allMonths = ImperialDate.Month.values()
        private val defaultDate = ImperialDate(0)

        private val WEEK_LENGTH = ImperialDate.DayOfWeek.values().size
        private const val YEAR_ROWS = 5
        private const val YEAR_COLUMNS = 4

        private fun calculateVisibleMonth(date: ImperialDate) = date.day.fold(
            { day -> ImperialDate.Month.values().first { it.standaloneDayAtTheBeginning == day } },
            { it.second }
        )
    }

    private class SavedState : BaseSavedState {
        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = Creator()
        }

        private class Creator: Parcelable.Creator<SavedState> {
            override fun createFromParcel(`in`: Parcel): SavedState? = SavedState(`in`)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

        val date: ImperialDate

        constructor(superState: Parcelable?, date: ImperialDate) : super(superState) {
            this.date = date
        }

        constructor(savedState: Parcel) : super(savedState) {
            date = savedState.readParcelable(ImperialDate::class.java.classLoader) ?: defaultDate
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            out.writeParcelable(date, 0)
        }
    }

    private val activeDayBackgroundColor = resources.getColor(R.color.colorPrimaryDark)

    private data class ActiveMonth(val month: ImperialDate.Month, val year: Int) {
        fun previousMonth() = when (month) {
            allMonths.first() -> ActiveMonth(allMonths.last(), year - 1)
            else -> ActiveMonth(allMonths[month.ordinal - 1], year)
        }

        fun nextMonth() = when (month) {
            allMonths.last() -> ActiveMonth(allMonths.first(), year + 1)
            else -> ActiveMonth(allMonths[month.ordinal + 1], year)
        }
    }

    var date: ImperialDate = date
        set(value) {
            if (field != value) {
                field = value
                redrawDays(activeMonth)
            }
        }

    private var activeMonth = ActiveMonth(calculateVisibleMonth(date), date.year)
        set(value) {
            field = value

            @SuppressLint("SetTextI18n")
            findViewById<TextView>(R.id.currentMonthName).text = value.month.readableName + ", " + value.year
            redrawDays(activeMonth)

            findViewById<View>(R.id.previousMonth).toggleVisibility(value.year > 1 || value.month.ordinal > 0)

            val size = YEAR_COLUMNS * YEAR_ROWS
            val firstYear = (value.year - 1) / size * size + 1

            activeYearRange = firstYear until firstYear + size
        }

    private lateinit var activeYearRange: IntRange

    init {
        isSaveEnabled = true
        inflate(context, R.layout.view_calendar, this)

        activeMonth = ActiveMonth(calculateVisibleMonth(date), date.year)

        findViewById<View>(R.id.previousMonth).setOnClickListener {
            activeMonth = activeMonth.previousMonth()
        }

        findViewById<View>(R.id.nextMonth).setOnClickListener {
            activeMonth = activeMonth.nextMonth()
        }

        findViewById<View>(R.id.previousYearRange).setOnClickListener {
            val size = YEAR_COLUMNS * YEAR_ROWS
            activeYearRange = activeYearRange.first - size..activeYearRange.last - size
            redrawYears()
        }

        findViewById<View>(R.id.nextYearRange).setOnClickListener {
            val size = YEAR_COLUMNS * YEAR_ROWS
            activeYearRange = activeYearRange.first + size..activeYearRange.last + size
            redrawYears()
        }

        findViewById<View>(R.id.currentMonthName).setOnClickListener {
            redrawYears()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        return SavedState(superState, date)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)

        if (state is SavedState) {
            date = state.date
        }
    }

    private fun redrawYears() {
        findViewById<View>(R.id.dayLayout).toggleVisibility(false)
        findViewById<View>(R.id.yearLayout).toggleVisibility(true)

        val rows = (0 until YEAR_ROWS).map { TableRow(context) }

        @SuppressLint("SetTextI18n")
        findViewById<TextView>(R.id.currentYearRange).text = "${activeYearRange.first} - ${activeYearRange.last}"

        findViewById<View>(R.id.previousYearRange).toggleVisibility(activeYearRange.first != 1)

        activeYearRange.forEach {
            rows[(it - 1) % YEAR_ROWS].addView(yearButton(it, it == activeMonth.year))
        }

        val yearTable = findViewById<TableLayout>(R.id.yearTable)

        yearTable.removeAllViews()
        rows.forEach(yearTable::addView)
    }

    private fun yearButton(yearNumber: Int, isActive: Boolean): FrameLayout {
        val styleRes = if (isActive) R.style.CalendarDay_Active else R.style.CalendarDay_Inactive
        val button = AppCompatButton(ContextThemeWrapper(context, styleRes), null, styleRes)

        if (isActive) {
            button.setBackgroundDrawable(ColorCircleDrawable(activeDayBackgroundColor))
        }

        button.text = yearNumber.toString()
        button.setOnClickListener {
            activeMonth = activeMonth.copy(year = yearNumber)
        }

        val layout = dayFieldLayout()
        layout.addView(button, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))

        return layout
    }

    private fun redrawDays(month: ActiveMonth) {
        findViewById<View>(R.id.yearLayout).toggleVisibility(false)
        findViewById<View>(R.id.dayLayout).toggleVisibility(true)

        val standaloneDay = month.month.standaloneDayAtTheBeginning

        val rows = ImperialDate.DayOfWeek.values().map {
            TableRow(context).apply {
                addView(TextView(context).apply {
                    setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
                    gravity = Gravity.CENTER_VERTICAL
                    text = it.readableName
                })
            }
        }

        val firstDayIndex =
            ImperialDate.of(1, month.month.ordinal + 1, month.year).dayOfWeek?.ordinal ?: 0

        (0 until firstDayIndex).forEach { dayIndex ->
            rows[dayIndex].addView(dayFieldLayout())
        }

        val activeDay =
            date.takeIf { it.year == month.year && calculateVisibleMonth(it) == month.month }
                ?.let { date -> date.day.fold({ 0 }, { it.first }) }

        val standaloneDayButton = findViewById<Button>(R.id.standaloneDayButton)
        standaloneDayButton.toggleVisibility(standaloneDay != null)

        if (standaloneDay != null) {
            standaloneDayButton.background
            standaloneDayButton.text = standaloneDay.readableName
            standaloneDayButton.setBackgroundColor(
                if (activeDay == 0)
                    activeDayBackgroundColor
                else resources.getColor(R.color.colorCardBackground)
            )
            standaloneDayButton.setTextColor(
                resources.getColor(
                    if (activeDay == 0) R.color.colorCardBackground else R.color.colorText
                )
            )
            standaloneDayButton.setOnClickListener {
                date = ImperialDate.of(standaloneDay, month.year)
            }
        }

        (1..month.month.numberOfDays).forEach { dayInMonth ->
            rows[(dayInMonth + firstDayIndex - 1) % WEEK_LENGTH].addView(
                dayButton(dayInMonth, month, dayInMonth == activeDay)
            )
        }

        val dayTable = findViewById<TableLayout>(R.id.dayTable)
        dayTable.removeAllViews()
        if (rows.first().childCount < 6) {
            rows.last().addView(fakeButton())
        }
        rows.forEach(dayTable::addView)
    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()

    private fun dayFieldLayout(): FrameLayout {
        val layoutParams = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER

        val layout = FrameLayout(context)
        layout.layoutParams = layoutParams

        return layout
    }

    private fun dayButton(dayNumber: Int, month: ActiveMonth, isActive: Boolean): FrameLayout {
        val styleRes = if (isActive) R.style.CalendarDay_Active else R.style.CalendarDay_Inactive
        val button = AppCompatButton(ContextThemeWrapper(context, styleRes), null, styleRes)

        if (isActive) {
            button.setBackgroundDrawable(ColorCircleDrawable(activeDayBackgroundColor))
        }

        button.text = dayNumber.toString()
        button.setOnClickListener {
            Timber.d("Selected day $dayNumber")
            date = ImperialDate.of(dayNumber, month.month.ordinal + 1, month.year)
        }

        val layout = dayFieldLayout()
        layout.addView(button, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))

        return layout
    }

    private fun fakeButton(): FrameLayout {
        val styleRes = R.style.CalendarDay_Inactive
        val button = AppCompatButton(ContextThemeWrapper(context, styleRes), null, styleRes)

        val layout = dayFieldLayout()
        layout.visibility = INVISIBLE
        layout.addView(button, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))

        return layout
    }
}
