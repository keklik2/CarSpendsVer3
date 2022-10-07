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
import com.demo.carspends.domain.settings.GetSettingValueUseCase
import com.demo.carspends.domain.settings.SetSettingUseCase
import com.demo.carspends.domain.settings.SettingsRepository
import com.demo.carspends.utils.*
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.files.fileSaver.DbSaver
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
    private val getSettingValueUseCase: GetSettingValueUseCase,
    private val setSettingUseCase: SetSettingUseCase,
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

    var isFirstLaunch = when(getSettingValueUseCase(SettingsRepository.SETTING_IS_FIRST_CAR_LAUNCH)) {
        SettingsRepository.FIRST_LAUNCH -> true
        else -> false
    }
    var tipsCount by state(0)
    fun nextTip() { tipsCount++ }

    val tips = mutableListOf(
        TipModel(resId = R.id.download_button, description = getString(R.string.tip_car_download_description)),
        TipModel(resId = R.id.upload_button, description = getString(R.string.tip_car_upload_description)),
        TipModel(resId = R.id.drop_car_button, description = getString(R.string.tip_car_drop_data_description))
    )

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

        autorun(::tipsCount) {
            if (it >= tips.size) {
                isFirstLaunch = false
                setSettingUseCase(
                    SettingsRepository.SETTING_IS_FIRST_CAR_LAUNCH,
                    SettingsRepository.NOT_FIRST_LAUNCH
                )
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

        withScope {
            carItem = newCar
            calculateAllMileage()
            calculateAvgPrice()

            updateCarItem()
            setCanCloseScreen()
        }
    }

    private fun calculateAllMileage() {
        carItem?.let { itCar ->
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

            carItem = itCar.copy(
                allMileage = newMileage,
                mileage = if (notes.isNotEmpty()) max(
                    itCar.startMileage,
                    notes.first().mileage
                ) else itCar.startMileage
            )
        }
    }

    private fun calculateAllPrice() {
        carItem?.let { itCar ->
            val allPrice = notesListForCalculation.sumOf { it.totalPrice }

            carItem = itCar.copy(allPrice = if (allPrice < 0) 0.0 else allPrice)
        }
    }

    private fun calculateAvgPrice() {
        carItem?.let { itCar ->
            val allPrice = if (itCar.allPrice > 0) itCar.allPrice else 0.0
            val allMileage = if (itCar.allMileage > 0) itCar.allMileage else 0

            carItem = itCar.copy(
                milPrice = if (allPrice <= 0.0 || allMileage <= 0) 0.0 else allPrice / allMileage
            )
        }
    }

    private fun calculateAvgFuel() {
        carItem?.let { itCar ->
            val notes = getFuelNotes().sortedByDescending { it.mileage }
            val newAvgFuel = if (notes.size >= 2) {
                val mileage = abs(notes.first().mileage - notes.last().mileage)
                val fuel = abs(notes.sumOf { it.liters } - notes.last().liters)
                if (fuel <= 0 || mileage <= 0) 0.0
                else fuel / (mileage / 100)
            } else 0.0

            carItem = itCar.copy(
                avgFuel = newAvgFuel
            )
        }
    }

    private fun calculateMomentFuel() {
        carItem?.let { itCar ->
            val sorted = getFuelNotes().sortedByDescending { it.mileage }
            val newMomentFuel = if (sorted.size >= 2) {
                val mileage = abs(sorted.first().mileage - sorted[1].mileage)
                val fuel = sorted.first().liters
                if (fuel <= 0 || mileage <= 0) 0.0
                else fuel / (mileage.toDouble() / 100)
            } else 0.0

            carItem = itCar.copy(
                momentFuel = newMomentFuel
            )
        }
    }

    private fun calculateAllFuel() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val newFuel = if (notes.isNotEmpty()) {
                val fuel = notes.sumOf { it.liters }
                if (fuel <= 0) 0.0
                else fuel
            } else 0.0

            carItem = itCar.copy(
                allFuel = newFuel
            )
        }
    }

    private fun calculateFuelPrice() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val newFuelPrice = if (notes.isNotEmpty()) {
                val price = notes.sumOf { it.totalPrice }
                if (price <= 0) 0.0
                else price
            } else 0.0

            carItem = itCar.copy(
                fuelPrice = newFuelPrice
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

    fun downloadNotes(saver: DbSaver<List<NoteItem>>?) = saver?.let { it.load() }

    fun applyNotes(notes: List<NoteItem>) {
        withScope {
            for (n in notes) {
                addNoteItemUseCase(n, listOf())
            }
            notesListForCalculation = getNoteItemsListByMileageUseCase().toMutableList()

            calculateAllMileage()
            calculateAllPrice()
            calculateAvgPrice()

            calculateAvgFuel()
            calculateMomentFuel()
            calculateAllFuel()
            calculateFuelPrice()

            updateCarItem()
        }
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
    private fun getNotExtraNotes() : List<NoteItem> = notesListForCalculation.filter {
        it.type != NoteType.EXTRA
    }

    private suspend fun updateCarItem() = carItem?.let { addCarItemUseCase(it) }
    private fun setCanCloseScreen() {
        canCloseScreen = true
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
