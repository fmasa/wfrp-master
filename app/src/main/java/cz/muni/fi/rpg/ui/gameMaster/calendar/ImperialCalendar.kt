package cz.muni.fi.rpg.ui.gameMaster.calendar

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.Saver
import androidx.compose.runtime.savedinstancestate.SaverScope
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.time.ImperialDate
import kotlinx.parcelize.Parcelize

@Composable
fun ImperialCalendar(date: ImperialDate, onDateChange: (ImperialDate) -> Unit) {
    Column {
        var activeScreen by savedInstanceState { ActiveScreen.DAYS_OF_MONTH }
        var activeMonth by savedInstanceState { ActiveMonth.forDate(date) }
        var activeYearRange by savedInstanceState(activeMonth, saver = IntRangeSaver()) {
            val size = YEAR_COLUMNS * YEAR_ROWS
            val firstYear = (activeMonth.year - 1) / size * size + 1

            firstYear until firstYear + size
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (activeScreen) {
                ActiveScreen.YEARS -> {
                    IconButton(
                        onClick = {
                            if (activeYearRange.first != 1) {
                                activeYearRange = activeYearRange.move(-YEAR_ROWS * YEAR_COLUMNS)
                            }
                        }) {
                        Icon(vectorResource(R.drawable.ic_caret_left))
                    }

                    Text(
                        "${activeYearRange.first} - ${activeYearRange.last}",
                        style = MaterialTheme.typography.h6
                    )

                    IconButton(onClick = {
                        activeYearRange = activeYearRange.move(+YEAR_ROWS * YEAR_COLUMNS)
                    }) {
                        Icon(vectorResource(R.drawable.ic_caret_right))
                    }
                }
                ActiveScreen.DAYS_OF_MONTH -> {
                    IconButton(onClick = { activeMonth = activeMonth.previousMonth() }) {
                        Icon(vectorResource(R.drawable.ic_caret_left))
                    }

                    Text(
                        activeMonth.toString(),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.clickable { activeScreen = ActiveScreen.YEARS },
                    )

                    IconButton(onClick = { activeMonth = activeMonth.nextMonth() }) {
                        Icon(vectorResource(R.drawable.ic_caret_right))
                    }
                }
            }
        }

        when (activeScreen) {
            ActiveScreen.YEARS -> YearPicker(
                selectedYear = activeMonth.year,
                firstYear = activeYearRange.first,
                onYearChange = {
                    activeMonth = activeMonth.copy(year = it)
                    activeScreen = ActiveScreen.DAYS_OF_MONTH
                })
            ActiveScreen.DAYS_OF_MONTH -> DayPicker(
                activeMonth,
                date,
                onDateChange
            )
        }
    }
}

@Composable
private fun YearPicker(
    selectedYear: Int,
    firstYear: Int,
    onYearChange: (Int) -> Unit
) {
    val activeYearRange = firstYear until firstYear + YEAR_COLUMNS * YEAR_ROWS

    Row {
        repeat(YEAR_COLUMNS) { column ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                repeat(YEAR_ROWS) { row ->
                    Box(contentAlignment = Alignment.Center) {
                        val year = activeYearRange.first + column * YEAR_ROWS + row

                        val isSelected = selectedYear == year

                        val modifier = if (isSelected)
                            Modifier.background(MaterialTheme.colors.primary, CircleShape)
                        else Modifier

                        IconButton(
                            onClick = { onYearChange(year) },
                            modifier = modifier
                        ) {
                            Text(
                                year.toString(),
                                color = if (isSelected)
                                    MaterialTheme.colors.onPrimary
                                else AmbientContentColor.current
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun StandaloneDay(
    day: ImperialDate.StandaloneDay,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val modifier = Modifier.fillMaxWidth()
    val dayName = day.readableName.toUpperCase(Locale.current)

    val inactiveTextColor = AmbientContentColor.current

    Box(Modifier.padding(bottom = 8.dp)) {
        if (selected) {
            Button(modifier = modifier, onClick = onClick) { Text(dayName) }
        } else {
            TextButton(modifier = modifier, onClick = onClick) {
                Text(dayName, color = inactiveTextColor)
            }
        }
    }
}

@Composable
private fun DayPicker(
    month: ActiveMonth,
    date: ImperialDate,
    onDateChange: (ImperialDate) -> Unit
) {
    val daysOfWeek = ImperialDate.DayOfWeek.values()
    val numberOfDays = month.month.numberOfDays

    Column(Modifier.fillMaxWidth()) {
        val selectedDayOfMonth = if (ActiveMonth.forDate(date) == month)
            date.day.fold({ 0 }, { it.first })
        else null

        month.month.standaloneDayAtTheBeginning?.let { day ->
            StandaloneDay(
                day,
                selected = selectedDayOfMonth == 0,
                onClick = { onDateChange(ImperialDate.of(day, month.year)) },
            )
        }

        Row {
            Column(
                Modifier.padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (day in daysOfWeek) {
                    Box(
                        modifier = Modifier.height(36.dp).padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(day.readableName, style = MaterialTheme.typography.caption)
                    }
                }
            }

            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth()) {
                    val daysOfPreviousMonth = listOfNulls(month.firstDay?.ordinal ?: 0)
                    val days = 1..numberOfDays

                    for (weekDays in (daysOfPreviousMonth + days).chunked(
                        daysOfWeek.size
                    )) {
                        Week(
                            weekDays,
                            onDaySelect = {
                                onDateChange(ImperialDate.of(it, month.month, month.year))
                            },
                            selectedDay = selectedDayOfMonth,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.Week(days: List<Int?>, selectedDay: Int?, onDaySelect: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f),
    ) {
        for (day in days) {
            val isSelected = selectedDay != null && day == selectedDay

            val modifier = if (isSelected)
                Modifier.background(MaterialTheme.colors.primary, CircleShape)
            else Modifier

            Box(
                modifier
                    .clickable(
                        onClick = { day?.let(onDaySelect) },
                        indication = RippleIndication(bounded = false, radius = 24.dp)
                    )
                    .size(36.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                day?.let {
                    Text(
                        it.toString(),
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected)
                            MaterialTheme.colors.onPrimary
                        else MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

@Immutable
@Parcelize
private data class ActiveMonth(val month: ImperialDate.Month, val year: Int) : Parcelable {
    companion object {
        fun forDate(date: ImperialDate): ActiveMonth = ActiveMonth(
            month = date.day.fold(
                { day ->
                    ImperialDate.Month.values().first { it.standaloneDayAtTheBeginning == day }
                },
                { it.second }
            ),
            year = date.year,
        )
    }

    val firstDay: ImperialDate.DayOfWeek?
        get() = ImperialDate.of(
            1,
            month.ordinal + 1,
            year
        ).dayOfWeek

    fun previousMonth() = when (month) {
        ImperialDate.Month.values().first() -> ActiveMonth(
            ImperialDate.Month.values().last(),
            year - 1
        )
        else -> ActiveMonth(ImperialDate.Month.values()[month.ordinal - 1], year)
    }

    fun nextMonth() = when (month) {
        ImperialDate.Month.values().last() -> ActiveMonth(
            ImperialDate.Month.values().first(),
            year + 1
        )
        else -> ActiveMonth(ImperialDate.Month.values()[month.ordinal + 1], year)
    }

    override fun toString() = "${month.readableName}, $year"
}

private class IntRangeSaver : Saver<IntRange, Bundle> {
    companion object {
        private const val START = "start"
        private const val END_INCLUSIVE = "endInclusive"
    }

    override fun restore(value: Bundle) = value.getInt(START)..value.getInt(END_INCLUSIVE)

    override fun SaverScope.save(value: IntRange) = bundleOf(
        START to value.first,
        END_INCLUSIVE to value.last,
    )
}

private enum class ActiveScreen {
    YEARS,
    DAYS_OF_MONTH
}

private fun listOfNulls(size: Int) = (0 until size).map { null }
private fun IntRange.move(modifier: Int) = first + modifier..last + modifier

private const val YEAR_ROWS = 5
private const val YEAR_COLUMNS = 4