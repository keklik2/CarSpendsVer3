package com.demo.carspends.presentation.fragments.car

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.Screens
import com.demo.carspends.data.database.car.CarItemDbModel
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.AddCarItemUseCase
import com.demo.carspends.domain.car.usecases.DropCarsDataUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.component.usecases.DropComponentsDataUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.DropNotesDataUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.utils.*
import com.demo.carspends.utils.files.fileSaver.DbSaver
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CarAddOrEditViewModel @Inject constructor(
    private val addCarItemUseCase: AddCarItemUseCase,
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val dropCarsDataUseCase: DropCarsDataUseCase,
    private val dropNotesDataUseCase: DropNotesDataUseCase,
    private val dropComponentsDataUseCase: DropComponentsDataUseCase,
    private val router: Router,
    private val app: Application
) : BaseViewModel(app) {

    fun exit() = router.exit()
    fun goToHomeScreen() = router.replaceScreen(Screens.HomePage())

    var cTitle: String? by state(null)
    var cMileage: String? by state(null)
    var cEngineCapacity: String? by state(null)
    var cPower: String? by state(null)

    var cId: Int? by state(null)
    var canCloseScreen by state(false)
    private var carItem: CarItem? by state(null)

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    private val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    private val _notesListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getNoteItemsListByMileageUseCase.invoke() }
    )
    private val notesListState by stateFromFlow(_notesListLoading.stateFlow)
    private var notesListForCalculation = mutableListOf<NoteItem>()

    init {
        autorun(::cId) {
            if (it != null) {
                _carsListLoading.refresh()
                _notesListLoading.refresh()
            } else carItem = null
        }

        autorun(::carsListState) { itState ->
            when (itState) {
                is Loading.State.Data -> {
                    cId?.let { itId ->
                        carItem = itState.data.firstOrNull { itCar ->
                            itCar.id == itId
                        }
                    }
                }
                is Loading.State.Error -> Toast.makeText(
                    app.applicationContext,
                    app.getString(R.string.toast_cars_loading_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        autorun(::notesListState) { itState ->
            when (itState) {
                is Loading.State.Data -> notesListForCalculation.addAll(itState.data)
            }
        }

        autorun(::carItem) {
            it?.let {
                cTitle = it.title
                cMileage = it.mileage.toString()
                cEngineCapacity = getFormattedDoubleAsStr(it.engineVolume)
                cPower = it.power.toString()
            }
        }
    }

    fun addOrEditCar(name: String?, mileage: String?, engineCapacity: String?, power: String?) {
        val rName = refactorString(name)
        val rMileage = refactorInt(mileage)
        val rEngineCapacity = refactorDouble(engineCapacity)
        val rPower = refactorInt(power)

        val newCar: CarItem = if (carItem != null) {
            val notes = notesListForCalculation
            val newStartMileage: Int = if (notes.isNotEmpty()) {
                if (notes.firstOrNull { it.type != NoteType.EXTRA } != null) carItem!!.startMileage
                else rMileage
            } else rMileage

            carItem!!.copy(
                title = rName,
                startMileage = newStartMileage,
                mileage = rMileage,
                engineVolume = rEngineCapacity,
                power = rPower
            )
        } else {
            CarItem(
                title = rName,
                startMileage = rMileage,
                mileage = rMileage,
                engineVolume = rEngineCapacity,
                power = rPower
            )
        }

        viewModelScope.launch {
            carItem = newCar
            calculateAllMileage()
            calculateAvgPrice()

            updateCarItem()
            setCanCloseScreen()
        }
    }

    private fun calculateAllMileage() {
        carItem?.let {
            val resMil = if (notesListForCalculation.isNotEmpty()) {
                val maxMil = max(it.mileage, notesListForCalculation[0].mileage)
                val lastNote = getLastNotExtraNote()
                val minMil =
                    if (lastNote != null) min(
                        it.startMileage,
                        lastNote.mileage
                    )
                    else it.startMileage
                abs(maxMil - minMil)
            } else 0

            carItem = it.copy(
                allMileage = resMil
            )
        }
    }

    private fun calculateAvgPrice() {
        carItem?.let {
            val newMilPrice =
                if (it.allPrice > 0 && it.allMileage > 0) {
                    val res = it.allPrice / it.allMileage
                    if (res < 0) 0.0
                    else res
                } else 0.0

            carItem = it.copy(
                milPrice = newMilPrice
            )
        }
    }

    fun saveNotes(saver: DbSaver<List<NoteItem>>?) {
        saver?.let {
            withScope {
                val notes = getNoteItemsListByMileageUseCase()
                saver.save(notes)
            }
        }
    }

    fun downloadNotes(saver: DbSaver<List<NoteItem>>?) {
        saver?.let {
            it.load()
        }
    }

    fun applyNotes(notes: List<NoteItem>) {
        withScope {
            for (n in notes) {
                addNoteItemUseCase(n, listOf())
            }
            notesListForCalculation = getNoteItemsListByMileageUseCase().toMutableList()

            calculateAllFuel()
            calculateAllFuelPrice()
            addAllPrice()
            calculateAvgFuel()
            calculateAllMileage()
            calculateAvgPrice()

            setCurrentMileage()
        }
    }

    private fun setCurrentMileage() {
        carItem?.let {
            max(getNotExtraNotes()?.firstOrNull()?.mileage ?: 0, it.mileage).apply {
                carItem = it.copy(
                    mileage = this
                )

                cMileage = this.toString()
            }

        }
    }

    private fun calculateAllFuelPrice() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val totalFuelPrice = max(
                notes.sumOf { it.totalPrice },
                0.0
            )

            carItem = itCar.copy(
                fuelPrice = totalFuelPrice
            )
        }
    }

    private fun calculateAllFuel() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val totalFuel = max(
                notes.sumOf { it.liters },
                0.0
            )

            carItem = itCar.copy(
                allFuel = totalFuel
            )
        }
    }

    private fun addAllPrice() {
        carItem?.let { itCar ->
            val allPrice = max(
                notesListForCalculation.sumOf { it.totalPrice },
                0.0
            )

            carItem = itCar.copy(
                allPrice = allPrice
            )
        }
    }

    private fun calculateAvgFuel() {
        carItem?.let { itCar ->
            if (notesListForCalculation.size > 1) {
                val listOfFuel = getFuelNotes()
                if (listOfFuel.size > 1) {
                    val note1 = listOfFuel[0]
                    val note2 = listOfFuel[1]
                    if (note2 != note1) {
                        val newMomentFuel = calculatedAvgFuelOfTwoNotes(note1, note2)
                        carItem = itCar.copy(
                            momentFuel = newMomentFuel,
                            avgFuel = calculateAvgFuelOfAll(listOfFuel)
                        )
                    }
                }
            }
        }
    }

    private fun calculatedAvgFuelOfTwoNotes(n1: NoteItem, n2: NoteItem): Double {
        val distance = maxOf(n1.mileage, n2.mileage) - minOf(n1.mileage, n2.mileage)
        val res = (n1.liters / distance) * 100
        return if (res > 0) {
            if (res == Double.POSITIVE_INFINITY) Double.MAX_VALUE
            else res
        } else 0.0
    }

    private fun calculateAvgFuelOfAll(listOfFuel: List<NoteItem>): Double {
        if (listOfFuel.size > 1) {
            val allMileage = listOfFuel.first().mileage - listOfFuel.last().mileage
            val allFuel = max(
                listOfFuel.sumOf { it.liters },
                0.0
            )

            val res = (allFuel / allMileage.toDouble()) * 100
            return if (res > 0) {
                if (res == Double.POSITIVE_INFINITY) Double.MAX_VALUE
                else res
            } else 0.0
        }
        return 0.0
    }

    fun dropCar() {
        withScope {
            dropNotesDataUseCase()
            carItem?.let {
                carItem = it.copy(
                    mileage = it.startMileage,
                    avgFuel = 0.0,
                    momentFuel = 0.0,
                    allFuel = 0.0,
                    fuelPrice = 0.0,
                    milPrice = 0.0,
                    allPrice = 0.0,
                    allMileage = 0
                )
            }
            updateCarItem()
        }
        router.replaceScreen(Screens.HomePage())
    }

    fun deleteCar() {
        withScope {
            dropNotesDataUseCase()
            dropComponentsDataUseCase()
            dropCarsDataUseCase()
            router.replaceScreen(Screens.HomePage())
        }
    }

    private fun getFuelNotes(): List<NoteItem> =
        notesListForCalculation.filter { it.type == NoteType.FUEL }
    private fun getLastNotExtraNote(): NoteItem? = notesListForCalculation.lastOrNull {
        it.type != NoteType.EXTRA
    }
    private fun getNotExtraNotes() : List<NoteItem>? = notesListForCalculation.filter {
        it.type != NoteType.EXTRA
    }

    private suspend fun updateCarItem() = carItem?.let { addCarItemUseCase(it) }
    private fun setCanCloseScreen() {
        canCloseScreen = true
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
