package com.demo.carspends.presentation.fragments.notesList

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.Screens
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.GetComponentItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.DeleteNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListUseCase
import com.demo.carspends.domain.settings.GetSettingValueUseCase
import com.demo.carspends.domain.settings.SetSettingUseCase
import com.demo.carspends.domain.settings.SettingsRepository
import com.demo.carspends.utils.NORMAL_LOADING_DELAY
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.dialogs.AppItemDialogContainer
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import com.demo.carspends.utils.ui.tipShower.TipModel
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class NotesListViewModel @Inject constructor(
    private val getNoteItemsListUseCase: GetNoteItemsListUseCase,
    private val deleteNoteItemUseCase: DeleteNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getSettingValueUseCase: GetSettingValueUseCase,
    private val setSettingUseCase: SetSettingUseCase,
    private val getComponentItemsListUseCase: GetComponentItemsListUseCase,
    private val router: Router,
    private val app: Application
) : BaseViewModel(app) {

    var carTitle by state(EMPTY_STR)
    var statisticsField1 by state(EMPTY_STR)
    var statisticsField2 by state(EMPTY_STR)
    var statisticsField1Img by state(R.drawable.ic_gas_station)
    var statisticsField2Img by state(R.drawable.ic_ruble)

    private var _noteType: NoteType? by state(null)
    var noteType: String by state(
        app.resources.getStringArray(R.array.note_type_values).toMutableList()[0]
    )
    private var _noteDate: Long by state(ALL_TIME)
    var noteDate: String by state(
        app.resources.getStringArray(R.array.date_values).toMutableList()[0]
    )

    private var _carId = CarItem.UNDEFINED_ID
    private var _carItem: CarItem? by state(null)

    private var notesListForCalculation = mutableListOf<NoteItem>()

    private fun goToCarAddFragment() = router.replaceScreen(Screens.CarEditOrAdd())
    fun goToCarEditFragment() = router.navigateTo(Screens.CarEditOrAdd(_carId))
    fun goToSettingsFragment() = router.navigateTo(Screens.Settings())
    fun goToNoteAddOrEditFragment(noteType: NoteType) {
        when (noteType) {
            NoteType.FUEL -> router.navigateTo(Screens.NoteFilling(_carId))
            NoteType.REPAIR -> router.navigateTo(Screens.NoteRepair(_carId))
            NoteType.EXTRA -> router.navigateTo(Screens.NoteExtra(_carId))
        }
    }

    fun goToNoteAddOrEditFragment(noteType: NoteType, id: Int) {
        when (noteType) {
            NoteType.FUEL -> router.navigateTo(Screens.NoteFilling(_carId, id))
            NoteType.REPAIR -> router.navigateTo(Screens.NoteRepair(_carId, id))
            NoteType.EXTRA -> router.navigateTo(Screens.NoteExtra(_carId, id))
        }
    }

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    private val _notesListLoading = OrdinaryLoading(
        viewModelScope,
        load = {
            getNoteItemsListUseCase.invoke(
                delay = NORMAL_LOADING_DELAY,
                type = _noteType,
                date = _noteDate
            )
        }
    )
    val notesListState by stateFromFlow(_notesListLoading.stateFlow)

    var isFirstLaunch =
        when (getSettingValueUseCase(SettingsRepository.SETTING_IS_FIRST_MAIN_LAUNCH)) {
            SettingsRepository.FIRST_LAUNCH -> true
            else -> false
        }
    var tipsCount by state(0)
    fun nextTip() {
        tipsCount++
    }

    val tips = mutableListOf(
        TipModel(
            resId = R.id.tv_car_title,
            description = getString(R.string.tip_car_title_description)
        ),
        TipModel(
            resId = R.id.tv_statistics_1,
            description = getString(R.string.tip_statistics_1_description)
        ),
        TipModel(
            resId = R.id.iv_settings,
            description = getString(R.string.tip_settings_description)
        ),
        TipModel(
            resId = R.id.fb_add_note,
            description = getString(R.string.tip_note_add_button_description)
        )
    )


    init {
        refreshData()

        withScope {
            notesListForCalculation = getNoteItemsListByMileageUseCase().toMutableList()
        }

        autorun(::carsListState) {
            when (it) {
                is Loading.State.Data -> {
                    if (it.data.isNotEmpty()) {
                        val car = it.data.first()
                        _carItem = car
                        _carId = car.id
                    } else goToCarAddFragment()
                }
                is Loading.State.Empty -> {
                    goToCarAddFragment()
                }
                is Loading.State.Error -> {
                }
                else -> {}
            }
        }

        autorun(::_carItem) {
            it?.let {
                statisticsField1 = getStatistic1Field(it)
                statisticsField2 = getStatistic2Field(it)
                carTitle = it.title
            }
        }

        autorun(::_noteType) {
            _notesListLoading.refresh()
        }

        autorun(::_noteDate) {
            _notesListLoading.refresh()
        }

        autorun(::tipsCount) {
            if (it >= tips.size) {
                isFirstLaunch = false
                setSettingUseCase(
                    SettingsRepository.SETTING_IS_FIRST_MAIN_LAUNCH,
                    SettingsRepository.NOT_FIRST_LAUNCH
                )
            }
        }
    }

    fun deleteNote(note: NoteItem) {
        withScope {
            val noteType = note.type
            deleteNoteItemUseCase(note)
            notesListForCalculation.clear()
            notesListForCalculation.addAll(getNoteItemsListByMileageUseCase())

            if (noteType != NoteType.EXTRA) calculateAllMileage()

            calculateAllPrice()
            calculateAvgPrice()

            if (noteType == NoteType.FUEL) {
                calculateAvgFuel()
                calculateMomentFuel()
                calculateAllFuel()
                calculateFuelPrice()
            }

            pushNewCarItem()
        }
    }

    private fun calculateAllMileage() {
        _carItem?.let { itCar ->
            val notes = getNotExtraNotes()
            val newMileage =
                if (notes.isNotEmpty())
                    abs(
                        max(
                            notes.maxOf { it.mileage },
                            itCar.startMileage
                        ) - min(notes.minOf { it.mileage }, itCar.startMileage)
                    )
                else 0

            _carItem = itCar.copy(
                allMileage = newMileage,
                mileage = if (notes.isNotEmpty()) max(
                    itCar.startMileage,
                    notes.first().mileage
                ) else itCar.startMileage
            )
        }
    }

    private fun calculateAllPrice() {
        _carItem?.let { itCar ->
            val allPrice = notesListForCalculation.sumOf { it.totalPrice }

            _carItem = itCar.copy(allPrice = if (allPrice < 0) 0.0 else allPrice)
        }
    }

    private fun calculateAvgPrice() {
        _carItem?.let { itCar ->
            val allPrice = if (itCar.allPrice > 0) itCar.allPrice else 0.0
            val allMileage = if (itCar.allMileage > 0) itCar.allMileage else 0

            _carItem = itCar.copy(
                milPrice = if (allPrice <= 0.0 || allMileage <= 0) 0.0 else allPrice / allMileage
            )
        }
    }

    private fun calculateAvgFuel() {
        _carItem?.let { itCar ->
            val notes = getFuelNotes().sortedByDescending { it.mileage }
            val newAvgFuel = if (notes.size >= 2) {
                val mileage = abs(notes.first().mileage - notes.last().mileage)
                val fuel = abs(notes.sumOf { it.liters } - notes.last().liters)
                if (fuel <= 0 || mileage <= 0) 0.0
                else fuel / (mileage / 100)
            } else 0.0

            _carItem = itCar.copy(
                avgFuel = newAvgFuel
            )
        }
    }

    private fun calculateMomentFuel() {
        _carItem?.let { itCar ->
            val sorted = getFuelNotes().sortedByDescending { it.mileage }
            val newMomentFuel = if (sorted.size >= 2) {
                val mileage = abs(sorted.first().mileage - sorted[1].mileage)
                val fuel = sorted.first().liters
                if (fuel <= 0 || mileage <= 0) 0.0
                else fuel / (mileage / 100)
            } else 0.0

            _carItem = itCar.copy(
                momentFuel = newMomentFuel
            )
        }
    }

    private fun calculateAllFuel() {
        _carItem?.let { itCar ->
            val notes = getFuelNotes()
            val newFuel = if (notes.isNotEmpty()) {
                val fuel = notes.sumOf { it.liters }
                if (fuel <= 0) 0.0
                else fuel
            } else 0.0

            _carItem = itCar.copy(
                allFuel = newFuel
            )
        }
    }

    private fun calculateFuelPrice() {
        _carItem?.let { itCar ->
            val notes = getFuelNotes()
            val newFuelPrice = if (notes.isNotEmpty()) {
                val price = notes.sumOf { it.totalPrice }
                if (price <= 0) 0.0
                else price
            } else 0.0

            _carItem = itCar.copy(
                fuelPrice = newFuelPrice
            )
        }
    }

    fun setData() {
        showItemListDialog(
            AppItemDialogContainer(
                R.array.date_values
            ) {
                when (it) {
                    0 -> data()
                    1 -> data(getYearDate())
                    2 -> data(getMonthDate())
                    else -> data(getWeekDate())
                }
                noteDate = app.resources.getStringArray(R.array.date_values).toMutableList()[it]
            }
        )
    }

    fun setType() {
        showItemListDialog(
            AppItemDialogContainer(
                R.array.note_type_values
            ) {
                when (it) {
                    0 -> type()
                    1 -> type(NoteType.FUEL)
                    2 -> type(NoteType.REPAIR)
                    else -> type(NoteType.EXTRA)
                }
                noteType = app.resources.getStringArray(R.array.note_type_values).toMutableList()[it]
            }
        )
    }

    private fun data(date: Long = ALL_TIME) {
        _noteDate = date
    }

    private fun type(type: NoteType? = null) {
        _noteType = type
    }

    fun refreshData() {
        _notesListLoading.refresh()
        _carsListLoading.refresh()
    }

    private fun pushNewCarItem() {
        viewModelScope.launch {
            _carItem?.let {
                editCarItemUseCase(it)
                refreshData()
            }
        }
    }

    private fun getStatistic1Field(car: CarItem): String {
        return when (getSettingValueUseCase(SettingsRepository.SETTING_STATISTIC_ONE)) {
            SettingsRepository.STATISTIC_AVG_FUEL -> {
                statisticsField1Img = IMG_FUEL_WHITE
                String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.avgFuel)
                )
            }
            SettingsRepository.STATISTIC_MOMENT_FUEL -> {
                statisticsField1Img = IMG_FUEL_WHITE
                String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.momentFuel)
                )
            }
            SettingsRepository.STATISTIC_ALL_FUEL -> {
                statisticsField1Img = IMG_FUEL_WHITE
                String.format(
                    app.getString(R.string.text_measure_gas_volume_unit_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.allFuel)
                )
            }
            SettingsRepository.STATISTIC_FUEL_PRICE -> {
                statisticsField1Img = IMG_FUEL_WHITE
                String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.fuelPrice)
                )
            }
            SettingsRepository.STATISTIC_MILEAGE_PRICE -> {
                statisticsField1Img = IMG_RUBLE_WHITE
                String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.milPrice)
                )
            }
            SettingsRepository.STATISTIC_ALL_PRICE -> {
                statisticsField1Img = IMG_RUBLE_WHITE
                String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.allPrice)
                )
            }
            else -> {
                statisticsField1Img = IMG_GEO_LOCATION_WHITE
                String.format(
                    app.getString(R.string.text_measure_mileage_unit_for_formatting),
                    getFormattedIntAsStrForDisplay(car.allMileage)
                )
            }
        }
    }

    private fun getStatistic2Field(car: CarItem): String {
        return when (getSettingValueUseCase(SettingsRepository.SETTING_STATISTIC_TWO)) {
            SettingsRepository.STATISTIC_AVG_FUEL -> {
                statisticsField2Img = IMG_FUEL
                String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.avgFuel)
                )
            }
            SettingsRepository.STATISTIC_MOMENT_FUEL -> {
                statisticsField2Img = IMG_FUEL
                String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.momentFuel)
                )
            }
            SettingsRepository.STATISTIC_ALL_FUEL -> {
                statisticsField2Img = IMG_FUEL
                String.format(
                    app.getString(R.string.text_measure_gas_volume_unit_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.allFuel)
                )
            }
            SettingsRepository.STATISTIC_FUEL_PRICE -> {
                statisticsField2Img = IMG_FUEL
                String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.fuelPrice)
                )
            }
            SettingsRepository.STATISTIC_MILEAGE_PRICE -> {
                statisticsField2Img = IMG_RUBLE
                String.format(
                    app.getString(R.string.text_measure_price_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.milPrice)
                )
            }
            SettingsRepository.STATISTIC_ALL_PRICE -> {
                statisticsField2Img = IMG_RUBLE
                String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(car.allPrice)
                )
            }
            else -> {
                statisticsField2Img = IMG_GEO_LOCATION
                String.format(
                    app.getString(R.string.text_measure_mileage_unit_for_formatting),
                    getFormattedIntAsStrForDisplay(car.allMileage)
                )
            }
        }
    }

    fun calculateComponentsResources() {
        withScope {
            val components = getComponentItemsListUseCase()
            val componentsToShow = mutableListOf<ComponentItem>()
            if (components.isNotEmpty()) {
                components.forEach {
                    if (getLeftComponentPercentage(it) <= MIN_COMPONENT_PERCENTAGE)
                        componentsToShow.add(it)
                }
            }

            showComponentWarning(componentsToShow)
        }
    }

    private fun showComponentWarning(
        components: MutableList<ComponentItem>
    ) {
        if (components.isNotEmpty()) {
            val component = components.first()
            components.removeAt(0)
            val per = getLeftComponentPercentage(component)
            showAlert(
                AppDialogContainer(
                    getString(R.string.dialog_low_component_title),
                    message = String.format(
                        getString(R.string.dialog_low_component),
                        component.title,
                        per.toString(),
                        getLeftComponentMileage(component).toString()
                    ),
                    onPositiveButtonClicked = { showComponentWarning(components) },
                    onNegativeButtonClicked = { showComponentWarning(components) }
                )
            )
        }
    }

    private fun getLeftComponentPercentage(component: ComponentItem): Int {
        _carItem?.let {
            val mil = it.mileage - component.startMileage
            return if (mil <= 0) 100
            else {
                val leftMil = component.resourceMileage - mil
                if (leftMil <= 0) 0
                else 100 - ((mil.toDouble() / component.resourceMileage.toDouble()) * 100.0).toInt()
            }
        }
        return 100
    }

    private fun getLeftComponentMileage(component: ComponentItem): Int {
        _carItem?.let {
            val mil = it.mileage - component.startMileage
            return if (mil <= 0) component.resourceMileage
            else {
                val leftMil = component.resourceMileage - mil
                if (leftMil <= 0) 0
                else leftMil
            }
        }
        return component.resourceMileage
    }

    /**
     * Additional functions
     */
    private fun getYearDate(): Long {
        return GregorianCalendar.getInstance().apply {
            add(GregorianCalendar.YEAR, MINUS_ONE)
        }.timeInMillis
    }

    private fun getMonthDate(): Long {
        return GregorianCalendar.getInstance().apply {
            add(GregorianCalendar.MONTH, MINUS_ONE)
        }.timeInMillis
    }

    private fun getWeekDate(): Long {
        return GregorianCalendar.getInstance().apply {
            add(GregorianCalendar.DATE, MINUS_WEEK)
        }.timeInMillis
    }

    private fun getFuelNotes(): List<NoteItem> =
        notesListForCalculation.filter { it.type == NoteType.FUEL }

    private fun getNotExtraNotes(): List<NoteItem> {
        return notesListForCalculation.filter { it.type != NoteType.EXTRA }
    }

    companion object {
        private const val IMG_FUEL = R.drawable.ic_gas_station
        private const val IMG_FUEL_WHITE = R.drawable.ic_gas_station_white
        private const val IMG_RUBLE = R.drawable.ic_ruble
        private const val IMG_RUBLE_WHITE = R.drawable.ic_ruble_white
        private const val IMG_GEO_LOCATION = R.drawable.ic_location
        private const val IMG_GEO_LOCATION_WHITE = R.drawable.ic_location_white

        private const val ALL_TIME = 0L
        private const val START_MIL = 0
        private const val START_AVG = 0.0

        private const val EMPTY_STR = ""

        private const val MIN_COMPONENT_PERCENTAGE = 10

        private const val MINUS_WEEK = -7
        private const val MINUS_ONE = -1
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
