package com.demo.carspends.presentation.fragments.noteExtra

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.utils.refactorDouble
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
import kotlin.math.max

class NoteExtraAddOrEditViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getCarsListUseCase: GetCarItemsListUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val router: Router,
    private val app: Application
) : AndroidViewModel(app), PropertyHost {

    fun goBack() = router.exit()

    var nTitle: String? by state(null)
    var nPrice: String? by state(null)
    var nDate by state(getCurrentDate())
    var nId: Int? by state(null)
    var cId: Int? by state(null)

    private var noteItem: NoteItem? by state(null)
    private var carItem: CarItem? by state(null)
    private val noteType = NoteType.EXTRA
    var canCloseScreen by state(false)

    private val _notesListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getNoteItemsListByMileageUseCase.invoke() }
    )
    private val notesListState by stateFromFlow(_notesListLoading.stateFlow)

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarsListUseCase.invoke() }
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
                nPrice = it.totalPrice.toString()
                nDate = it.date
            } else {
                nTitle = null
                nPrice = null
                nDate = getCurrentDate()
            }
        }
    }

    fun addOrEditNoteItem(title: String?, price: String?) {
        val rTitle = refactorString(title)
        val rPrice = refactorDouble(price)

        val newNote = noteItem?.copy(
            title = rTitle,
            totalPrice = rPrice,
            date = nDate
        ) ?: NoteItem(
            title = rTitle,
            totalPrice = rPrice,
            date = nDate,
            type = noteType
        )

        viewModelScope.launch {
            addNoteItemUseCase(newNote)
            _notesListLoading.refresh()

            addAllPrice()
            calculateAvgPrice()
            updateCarItem()
            setCanCloseScreen()
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

    private suspend fun addAllPrice() {
        carItem?.let { itCar ->
            getNoteItemsListByMileageUseCase().let { itList ->
                val newAllPrice = max(
                    itList.sumOf { it1 -> it1.totalPrice},
                    0.0
                )
                carItem = itCar.copy(
                    allPrice = newAllPrice
                )
            }
        }
    }

    private suspend fun updateCarItem() = carItem?.let { editCarItemUseCase(it) }
    private fun getCurrentDate(): Long = Date().time
    private fun setCanCloseScreen() { canCloseScreen = true }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
