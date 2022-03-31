package com.demo.carspends.presentation.fragments.noteRepair

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.refactorString
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NoteRepairAddOrEditViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val editNoteItemUseCase: EditNoteItemUseCase,
    private val getNoteItemUseCase: GetNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getCarItemUseCase: GetCarItemUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val router: Router
) : ViewModel() {

    fun goBack() = router.exit()

    private val noteType = NoteType.REPAIR
    private var carId = CarItem.UNDEFINED_ID

    private val _noteDate = MutableLiveData<Long>()
    val noteDate get() = _noteDate

    private val _noteItem = MutableLiveData<NoteItem>()
    val noteItem get() = _noteItem

    private val _currCarItem = MutableLiveData<CarItem>()
    val currCarItem get() = _currCarItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _noteDate.value = Date().time
    }

    fun addNoteItem(title: String?, totalPrice: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rTotalPrice = refactorDouble(totalPrice)
        val rMileage = refactorInt(mileage)

        viewModelScope.launch {
            val nDate = _noteDate.value
            if (nDate != null) {
                val newNote = NoteItem(
                    title = rTitle,
                    totalPrice = rTotalPrice,
                    mileage = rMileage,
                    date = nDate,
                    type = noteType
                )
                addNoteItemUseCase(newNote)

                updateMileage(rMileage)
                calculateAllMileage()
                addLastPrice(newNote)
                calculateAvgPrice()
                setCanCloseScreen()
            } else throw Exception(ERR_NULL_ITEM_ADD)
        }
    }

    fun editNoteItem(title: String?, totalPrice: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rTotalPrice = refactorDouble(totalPrice)
        val rMileage = refactorInt(mileage)

        viewModelScope.launch {
            val nItem = _noteItem.value
            if (nItem != null) {
                val nDate = _noteDate.value
                if (nDate != null) {
                    editNoteItemUseCase(
                        nItem.copy(
                            title = rTitle,
                            totalPrice = rTotalPrice,
                            mileage = rMileage,
                            date = nDate,
                            type = noteType
                        )
                    )

                    rollbackCarMileage(nItem)
                    calculateAllMileage()
                    addAllPrice()
                    calculateAvgPrice()
                    setCanCloseScreen()
                } else throw Exception(ERR_NULL_ITEM_EDIT)
            } else throw Exception(ERR_NULL_ITEM_EDIT)
        }
    }

    private suspend fun calculateAllMileage() {
        val carItem = getCarItemUseCase(carId)
        val notes = getNoteItemsListByMileageUseCase()

        val resMil = if (notes.isNotEmpty()) {
            val maxMil = max(carItem.mileage, notes[0].mileage)
            val lastNote = getLastNotExtraNote()
            val minMil =
                if (lastNote != null) min(
                    carItem.startMileage,
                    lastNote.mileage
                ) else carItem.startMileage
            abs(maxMil - minMil)
        } else 0

        editCarItemUseCase(
            carItem.copy(
                allMileage = resMil
            )
        )
    }

    private suspend fun getLastNotExtraNote(): NoteItem? {
        val notes = getNoteItemsListByMileageUseCase()
        for (i in notes.reversed()) {
            if (i.type != NoteType.EXTRA) return i
        }
        return null
    }

    private suspend fun addLastPrice(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        editCarItemUseCase(
            carItem.copy(
                allPrice = carItem.allPrice + note.totalPrice
            )
        )
    }

    private suspend fun addAllPrice() {
        var allPrice = 0.0
        for (i in getNoteItemsListByMileageUseCase()) {
            allPrice += i.totalPrice
        }

        if (allPrice < 0) allPrice = 0.0

        editCarItemUseCase(
            getCarItemUseCase(carId).copy(
                allPrice = allPrice
            )
        )
    }

    private suspend fun rollbackCarMileage(oldItem: NoteItem) {
        val cItem = getCarItemUseCase(carId)
        if (oldItem.mileage == cItem.mileage) {
            val notesList = getNoteItemsListByMileageUseCase()
            var newMileage = cItem.startMileage
            if (notesList.isNotEmpty()) {
                for (i in notesList) {
                    if (i.type != NoteType.EXTRA && i.mileage > newMileage) newMileage = i.mileage
                }
            }
            editCarItemUseCase(
                cItem.copy(
                    mileage = newMileage
                )
            )
            updateCarItem()
        }
    }

    private suspend fun calculateAvgPrice() {
        val carItem = getCarItemUseCase(carId)
        val newMilPrice =
            if (carItem.allPrice > 0 && carItem.allMileage > 0) {
                val res = carItem.allPrice / carItem.allMileage
                if (res < 0) 0.0
                else res
            } else 0.0

        editCarItemUseCase(
            carItem.copy(
                milPrice = newMilPrice
            )
        )
    }

    private suspend fun updateMileage(newMileage: Int) {
        val cItem = getCarItemUseCase(carId)
        val oldMileage = cItem.mileage
        if (newMileage > oldMileage) {
            editCarItemUseCase(
                cItem.copy(mileage = newMileage)
            )
        }
        updateCarItem()
    }

    fun setCarItem(id: Int) {
        viewModelScope.launch {
            carId = id
            _currCarItem.value = getCarItemUseCase.invoke(carId)
        }
    }

    private suspend fun updateCarItem() {
        _currCarItem.value = getCarItemUseCase.invoke(carId)
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getNoteItemUseCase(id)
            _noteItem.value = item
            _noteDate.value = item.date
        }
    }

    fun setNoteDate(date: Long) {
        _noteDate.value = date
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val ERR_NULL_ITEM_EDIT = "Received NULL NoteItem for EditNoteItemUseCase()"
        private const val ERR_NULL_ITEM_ADD = "Received NULL NoteItem for AddNoteItemUseCase()"
    }
}
