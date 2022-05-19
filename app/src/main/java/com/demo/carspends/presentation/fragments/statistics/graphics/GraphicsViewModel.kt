package com.demo.carspends.presentation.fragments.statistics.graphics

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.GetNoteItemsListUseCase
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDayOfWeekRes
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.getFormattedYear
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import java.util.*
import javax.inject.Inject

class GraphicsViewModel @Inject constructor(
    private val getNoteItemsListUseCase: GetNoteItemsListUseCase,
    private val app: Application
) : BaseViewModel(app) {

    var testGraphItem by state(mutableListOf<GraphItem>())

    var dateType by state(DATE_WEEK)
    private val dateFilter by computed(::dateType) {
        when (it) {
            DATE_ALL_TIME -> ALL_TIME
            DATE_YEAR -> GregorianCalendar.getInstance().apply {
                add(GregorianCalendar.YEAR, MINUS_YEAR)
            }.timeInMillis
            DATE_MONTH -> GregorianCalendar.getInstance().apply {
                add(GregorianCalendar.MONTH, MINUS_MONTH)
            }.timeInMillis
            else -> GregorianCalendar.getInstance().apply {
                add(GregorianCalendar.DATE, MINUS_WEEK)
            }.timeInMillis
        }
    }

    init {
        getNewGraphList()

        autorun(::dateFilter) {
            getNewGraphList()
        }
    }

    private fun getNewGraphList() {
        withScope {
            val test = mutableListOf<GraphItem>().apply {
                getAvgFuel()?.let { add(it) }
                getAllPriceWithoutFuel()?.let { add(it) }
                getAllPrice()?.let { add(it) }
                getAllMileage()?.let { add(it) }
            }
            testGraphItem = test
        }
    }

    private fun areDaysTheSame(date1: Long, date2: Long): Boolean =
        getFormattedDayOfWeekRes(date1) == getFormattedDayOfWeekRes(date2)

    private suspend fun getAvgFuel(): GraphItem? {
        val notes = getNoteItemsListUseCase(type = NoteType.FUEL, date = dateFilter)

        return if (notes.isNotEmpty()) {
            val measure = String.format(
                getString(STR_AVG_FUEL),
                getFormattedIntAsStrForDisplay(notes.sumOf { it.liters }.toInt())
            )
            val maxHeight = notes.maxOf { it.liters }.toInt()

            val titlesList = mutableListOf<String>()
            val dataList = mutableListOf<Int>()

            when (dateType) {
                DATE_ALL_TIME -> {
                    return null
                }
                DATE_YEAR -> {
                    return null
                }
                DATE_MONTH -> {
                    return null
                }
                else -> {
                    var lastNoteDate = notes.first().date
                    for (n in notes) {
                        val day = getString(getFormattedDayOfWeekRes(n.date))
                        var mileage = n.liters.toInt()

                        if (n == notes.first() || !areDaysTheSame(lastNoteDate, n.date)) {
                            titlesList.add(day)
                            dataList.add(mileage)
                        } else {
                            mileage += dataList.last()
                            dataList.remove(dataList.size - 1)
                            dataList.add(mileage)
                        }

                        lastNoteDate = n.date
                    }
                }
            }
            GraphItem(
                getString(TITLE_AVG_FUEL),
                measure,
                maxHeight,
                AVG_FUEL_ICON,
                titlesList.apply { sortDescending() },
                dataList.apply { sortDescending() }
            )
        } else null
    }

    private suspend fun getAllPriceWithoutFuel(): GraphItem? {
        val notes = getNoteItemsListUseCase(date = dateFilter).filter {
            it.type != NoteType.FUEL
        }

        return if (notes.isNotEmpty()) {
            val measure = String.format(
                getString(STR_ALL_PRICE),
                getFormattedIntAsStrForDisplay(
                    notes.sumOf { it.totalPrice }.toInt()
                )
            )
            val maxHeight = notes.maxOf { it.totalPrice }.toInt()

            val titlesList = mutableListOf<String>()
            val dataList = mutableListOf<Int>()

            when (dateType) {
                DATE_ALL_TIME -> {
                    return null
                }
                DATE_YEAR -> {
                    return null
                }
                DATE_MONTH -> {
                    return null
                }
                else -> {
                    var lastNoteDate = notes.first().date
                    for (n in notes) {
                        val day = getString(getFormattedDayOfWeekRes(n.date))
                        var price = n.totalPrice.toInt()

                        if (n == notes.first() || !areDaysTheSame(lastNoteDate, n.date)) {
                            titlesList.add(day)
                            dataList.add(price)
                        } else {
                            price += dataList.last()
                            dataList.remove(dataList.size - 1)
                            dataList.add(price)
                        }

                        lastNoteDate = n.date
                    }
                }
            }
            GraphItem(
                getString(TITLE_ALL_PRICE_WITHOUT_FUEL),
                measure,
                maxHeight,
                ALL_PRICE_ICON,
                titlesList.apply { sortDescending() },
                dataList.apply { sortDescending() }
            )
        } else null
    }

    private suspend fun getAllPrice(): GraphItem? {
        val notes = getNoteItemsListUseCase(date = dateFilter)

        return if (notes.isNotEmpty()) {
            val measure =
                String.format(
                    getString(STR_ALL_PRICE),
                    getFormattedIntAsStrForDisplay(notes.sumOf { it.totalPrice }.toInt())
                )
            val maxHeight = notes.maxOf { it.totalPrice }.toInt()

            val titlesList = mutableListOf<String>()
            val dataList = mutableListOf<Int>()

            when (dateType) {
                DATE_ALL_TIME -> {
                    return null
                }
                DATE_YEAR -> {
                    return null
                }
                DATE_MONTH -> {
                    return null
                }
                else -> {
                    var lastNoteDate = notes.first().date
                    for (n in notes) {
                        val day = getString(getFormattedDayOfWeekRes(n.date))
                        var price = n.totalPrice.toInt()

                        if (n == notes.first() || !areDaysTheSame(lastNoteDate, n.date)) {
                            titlesList.add(day)
                            dataList.add(price)
                        } else {
                            price += dataList.last()
                            dataList.remove(dataList.size - 1)
                            dataList.add(price)
                        }

                        lastNoteDate = n.date
                    }
                }
            }
            GraphItem(
                getString(TITLE_ALL_PRICE),
                measure,
                maxHeight,
                ALL_PRICE_ICON,
                titlesList.apply { sortDescending() },
                dataList.apply { sortDescending() }
            )
        } else null
    }

    private suspend fun getAllMileage(): GraphItem? {
        val notes = getNoteItemsListUseCase(date = dateFilter).filter {
            it.type != NoteType.EXTRA
        }

        return if (notes.isNotEmpty()) {
            val measure =
                String.format(
                    getString(STR_ALL_MILEAGE),
                    getFormattedIntAsStrForDisplay(notes.sumOf { it.mileage })
                )
            val maxHeight = notes.maxOf { it.mileage }

            val titlesList = mutableListOf<String>()
            val dataList = mutableListOf<Int>()

            when (dateType) {
                DATE_ALL_TIME -> {
                    return null
                }
                DATE_YEAR -> {
                    return null
                }
                DATE_MONTH -> {
                    return null
                }
                else -> {
                    var lastNoteDate = notes.first().date
                    for (n in notes) {
                        val day = getString(getFormattedDayOfWeekRes(n.date))
                        var mileage = n.mileage

                        if (n == notes.first() || !areDaysTheSame(lastNoteDate, n.date)) {
                            titlesList.add(day)
                            dataList.add(mileage)
                        } else {
                            mileage += dataList.last()
                            dataList.remove(dataList.size - 1)
                            dataList.add(mileage)
                        }

                        lastNoteDate = n.date
                    }
                }
            }
            GraphItem(
                getString(TITLE_ALL_MILEAGE),
                measure,
                maxHeight,
                ALL_MILEAGE_ICON,
                titlesList.apply { sortDescending() },
                dataList.apply { sortDescending() }
            )
        } else null
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope

    companion object {
        private const val STR_AVG_FUEL = R.string.text_measure_gas_charge_for_formatting
        private const val STR_ALL_PRICE = R.string.text_measure_currency_for_formatting
        private const val STR_ALL_MILEAGE = R.string.text_measure_mileage_unit_for_formatting

        private const val TITLE_AVG_FUEL = R.string.text_avg_fuel
        private const val TITLE_ALL_PRICE = R.string.text_all_price
        private const val TITLE_ALL_PRICE_WITHOUT_FUEL = R.string.text_all_price_without_fuel
        private const val TITLE_ALL_MILEAGE = R.string.text_all_mileage

        private const val AVG_FUEL_ICON = R.drawable.ic_baseline_local_gas_station_24_white
        private const val ALL_PRICE_ICON = R.drawable.ic_baseline_currency_ruble_white_24
        private const val ALL_MILEAGE_ICON = R.drawable.ic_baseline_location_on_white_24

        private const val DATE_WEEK = 1
        private const val DATE_MONTH = 2
        private const val DATE_YEAR = 3
        private const val DATE_ALL_TIME = 4

        private const val ALL_TIME = 0L
        private const val MINUS_YEAR = -1
        private const val MINUS_MONTH = -1
        private const val MINUS_WEEK = -7
    }
}
