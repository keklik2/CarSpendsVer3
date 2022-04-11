package com.demo.carspends.presentation.fragments.notesList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.Screens
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.DeleteNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListUseCase
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.loading.simple.state
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
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
    private val router: Router,
    app: Application
) : AndroidViewModel(app), PropertyHost {

    var carTitle by state(EMPTY_STR)
    var statisticsField1 by state(EMPTY_STR)
    var statisticsField2 by state(EMPTY_STR)

    private var noteType: NoteType? by state(null)
    private var noteDate: Long by state(ALL_TIME)

    private var _carId = CarItem.UNDEFINED_ID
    private var _carItem: CarItem? by state(null)

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
        load = { getNoteItemsListUseCase.invoke(noteType, noteDate)
        }
    )
    val notesListState by stateFromFlow(_notesListLoading.stateFlow)


    init {
        refreshData()

        autorun(::carsListState) {
            when (it) {
                is Loading.State.Data -> {
                    if (it.data.isNotEmpty()) {
                        val car = it.data.first()
                        _carItem = car
                        _carId = car.id
                    } else goToCarAddFragment()
                }
                is Loading.State.Empty -> goToCarAddFragment()
                else -> {}
            }
        }

        autorun(::_carItem) {
            it?.let {
                statisticsField1 = String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.momentFuel)
                )
                statisticsField2 = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.milPrice)
                )
                carTitle = it.title
            }
        }

        autorun(::noteType) {
            _notesListLoading.refresh()
        }

        autorun(::noteDate) {
            _notesListLoading.refresh()
        }
    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            val noteType = note.type
            deleteNoteItemUseCase(note)

            if (noteType != NoteType.EXTRA) {
                rollbackCarMileage()
                calculateAllMileage()

                if (noteType == NoteType.FUEL) {
                    calculateAvgFuel()
                    calculateAllFuel(note)
                    calculateAllFuelPrice(note)
                }
            }

            rollbackAllPrice(note)
            calculateAvgPrice()
            pushNewCarItem()
        }
    }

    private  fun calculateAllFuelPrice(note: NoteItem) {
        _carItem?.let {
            val price = it.fuelPrice - note.totalPrice
            val finalAllFuelPrice =
                if (price < 0) 0.0
                else price

            _carItem = it.copy(
                fuelPrice = finalAllFuelPrice
            )
        }
    }

    private fun calculateAllFuel(note: NoteItem) {
        _carItem?.let {
            val liters = it.allFuel - note.liters
            val finalAllFuel =
                if (liters < 0) 0.0
                else liters

            _carItem = it.copy(
                allFuel = finalAllFuel
            )
        }
    }

    private suspend fun calculateAllMileage() {
        _carItem?.let {
            val notes = getNoteItemsListByMileageUseCase()

            val resMil = if (notes.isNotEmpty()) {
                val maxMil = max(it.mileage, notes[0].mileage)
                val minMil =
                    if (notes[notes.size - 1].type != NoteType.EXTRA) min(
                        it.startMileage,
                        notes[notes.size - 1].mileage
                    )
                    else it.startMileage
                abs(maxMil - minMil)
            } else 0

            _carItem = it.copy(
                allMileage = resMil
            )
        }
    }

    private fun rollbackAllPrice(note: NoteItem) {
        _carItem?.let {

            _carItem = it.copy(
                allPrice = it.allPrice - note.totalPrice
            )
        }
    }

    private suspend fun calculateAvgFuel() {
        val notes = getNoteItemsListByMileageUseCase()
        _carItem?.let {
            var momentFuel = START_AVG
            var avgFuel = START_AVG
            if (notes.size > 1) {
                val listOfFuel = mutableListOf<NoteItem>()
                for (i in notes) {
                    if (i.type == NoteType.FUEL) listOfFuel.add(i)
                }
                if (listOfFuel.size > 1) {
                    val note1 = listOfFuel[0]
                    val note2 = listOfFuel[1]
                    if (note2 != note1) {
                        momentFuel = calculatedAvgFuelOfTwoNotes(note1, note2)
                        avgFuel = calculateAvgFuelOfAll(listOfFuel)
                    }
                }
            }

            _carItem = it.copy(
                momentFuel = momentFuel,
                avgFuel = avgFuel
            )
        }
    }

    private fun calculateAvgPrice() {
        _carItem?.let {
            val newMilPrice =
                if (it.allPrice > 0 && it.allMileage > 0) it.allPrice / it.allMileage
                else 0.0

            _carItem = it.copy(
                milPrice = newMilPrice
            )
        }
    }

    private fun calculateAvgFuelOfAll(listOfFuel: List<NoteItem>): Double {
        if (listOfFuel.size > 1) {
            val allMileage = listOfFuel[0].mileage - listOfFuel[listOfFuel.size - 1].mileage
            var allFuel = 0.0
            for (i in 0 until listOfFuel.size - 1) {
                allFuel += listOfFuel[i].liters
            }

            val res = (allFuel / allMileage.toDouble()) * 100
            return if (res > 0) res
            else 0.0
        }
        return 0.0
    }

    private fun calculatedAvgFuelOfTwoNotes(n1: NoteItem, n2: NoteItem): Double {
        val distance = maxOf(n1.mileage, n2.mileage) - minOf(n1.mileage, n2.mileage)
        val res = (n1.liters / distance) * 100
        return if (res < 0) 0.0
        else res
    }

    private suspend fun rollbackCarMileage() {
        _carItem?.let {
            val notesList = getNoteItemsListByMileageUseCase()
            var newMileage = it.startMileage
            if (notesList.isNotEmpty()) {
                for (i in notesList) {
                    if (i.type != NoteType.EXTRA && i.mileage > newMileage) newMileage = i.mileage
                }
            }

            _carItem = it.copy(
                mileage = newMileage
            )
        }
    }

    fun setData(date: Long = ALL_TIME) {
        noteDate = date
    }

    fun setType(type: NoteType? = null) {
        noteType = type
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

    companion object {
        private const val ALL_TIME = 0L
        private const val START_MIL = 0
        private const val START_AVG = 0.0

        private const val EMPTY_STR = ""
        private const val UNDEFINED_ID = -1
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
