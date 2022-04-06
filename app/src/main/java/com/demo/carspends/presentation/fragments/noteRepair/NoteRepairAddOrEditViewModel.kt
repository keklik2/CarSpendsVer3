package com.demo.carspends.presentation.fragments.noteRepair

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.refactorString
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NoteRepairAddOrEditViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val router: Router,
    private val app: Application
) : AndroidViewModel(app), PropertyHost {

    fun goBack() = router.exit()

    var nTitle: String? by state(null)
    var nPrice: String? by state(null)
    var nMileage: String? by state(null)
    var nDate by state(getCurrentDate())
    var nId: Int? by state(null)
    var cId: Int? by state(null)

    private var noteItem: NoteItem? by state(null)
    private var carItem: CarItem? by state(null)
    private val noteType = NoteType.REPAIR
    var canCloseScreen by state(false)

    private val _notesListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getNoteItemsListByMileageUseCase.invoke() }
    )
    private val notesListState by stateFromFlow(_notesListLoading.stateFlow)
    private var notesListForCalculation = mutableListOf<NoteItem>()

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    private val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    init {
        autorun(::nId) {
            if (it != null) _notesListLoading.refresh()
            else {
                noteItem = null
                nDate = getCurrentDate()
            }
        }

        autorun(::cId) {
            if (it != null) _carsListLoading.refresh()
            else carItem = null
        }

        autorun(::notesListState) { itState ->
            when (itState) {
                is Loading.State.Data -> {
                    nId?.let { itId ->
                        noteItem = itState.data.firstOrNull { itNote ->
                            itNote.id == itId
                        }
                    }
                    notesListForCalculation.clear()
                    notesListForCalculation.addAll(itState.data)
                }
                is Loading.State.Error -> Toast.makeText(
                    app.applicationContext,
                    app.getString(R.string.toast_notes_loading_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
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
            }
        }

        autorun(::noteItem) {
            if (it != null) {
                nTitle = it.title
                nPrice = getFormattedDoubleAsStr(it.totalPrice)
                nMileage = it.mileage.toString()
                nDate = it.date
            } else {
                nTitle = null
                nPrice = null
                nMileage = null
                nDate = getCurrentDate()
            }
        }

        autorun(::carItem) {
            it?.let {
                if(nMileage == null) nMileage = it.mileage.toString()
            }
        }
    }

    fun addOrEditNoteItem(title: String?, totalPrice: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rTotalPrice = refactorDouble(totalPrice)
        val rMileage = refactorInt(mileage)

        val newNote = noteItem?.copy(
            title = rTitle,
            totalPrice = rTotalPrice,
            mileage = rMileage,
            date = nDate,
            type = noteType
        ) ?: NoteItem(
            title = rTitle,
            totalPrice = rTotalPrice,
            mileage = rMileage,
            date = nDate,
            type = noteType
        )

        viewModelScope.launch {
            addNoteItemUseCase(newNote)
            notesListForCalculation.clear()
            notesListForCalculation.addAll(getNoteItemsListByMileageUseCase())

            if (noteItem == null) {
                updateMileage(rMileage)
                addLastPrice(newNote)
            } else {
                noteItem = newNote
                rollbackCarMileage()
                addAllPrice()
            }

            calculateAllMileage()
            calculateAvgPrice()

            updateCarItem()
            setCanCloseScreen()
        }
    }

    private fun getLastNotExtraNote(): NoteItem? {
        return notesListForCalculation.lastOrNull {
            it.type != NoteType.EXTRA
        }
    }

    private fun calculateAllMileage() {
        carItem?.let { itCar ->
            val resMil = if (notesListForCalculation.isNotEmpty()) {
                val maxMil = max(itCar.mileage, notesListForCalculation[0].mileage)
                val lastNote = getLastNotExtraNote()
                val minMil =
                    if (lastNote != null) min(
                        itCar.startMileage,
                        lastNote.mileage
                    ) else itCar.startMileage
                abs(maxMil - minMil)
            } else 0

            carItem = itCar.copy(
                allMileage = resMil
            )
        }

    }

    private fun addLastPrice(note: NoteItem) {
        carItem?.let {
            carItem = it.copy(
                allPrice = it.allPrice + note.totalPrice
            )
        }
    }

    private fun addAllPrice() {
        carItem?.let {
            val allPrice = max(
                notesListForCalculation.sumOf { it.totalPrice },
                0.0
            )

            carItem = it.copy(
                allPrice = allPrice
            )
        }
    }

    private fun calculateAvgPrice() {
        carItem?.let { itCar ->
            val newMilPrice =
                if (itCar.allPrice > 0 && itCar.allMileage > 0) {
                    val res = itCar.allPrice / itCar.allMileage
                    if (res < 0) 0.0
                    else res
                } else 0.0

            carItem = itCar.copy(
                milPrice = newMilPrice
            )
        }
    }

    private fun rollbackCarMileage() {
        carItem?.let { itCar ->
            val newMileage: Int = when (val item = notesListForCalculation.firstOrNull {
                it.type != NoteType.EXTRA
                        && it.mileage > itCar.startMileage
            }) {
                null -> itCar.startMileage
                else -> item.mileage
            }

            carItem = itCar.copy(
                mileage = newMileage
            )
        }
    }

    private fun updateMileage(newMileage: Int) {
        carItem?.let { itCar ->
            carItem = itCar.copy(
                mileage = max(newMileage, itCar.mileage)
            )
        }
    }

    private suspend fun updateCarItem() = carItem?.let { editCarItemUseCase(it) }
    private fun getCurrentDate(): Long = Date().time
    private fun setCanCloseScreen() {
        canCloseScreen = true
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
