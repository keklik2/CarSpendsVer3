package com.demo.carspends.presentation.fragments.noteRepair

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.picture.DeletePictureUseCase
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.refactorString
import com.demo.carspends.utils.ui.baseViewModel.NoteAddOrEditViewModel
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NoteRepairAddOrEditViewModel @Inject constructor(
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val addNoteItemUseCase: AddNoteItemUseCase,
    getNoteItemUseCase: GetNoteItemUseCase,
    getCarItemUseCase: GetCarItemUseCase,
    editCarItemUseCase: EditCarItemUseCase,
    deletePictureUseCase: DeletePictureUseCase,
    router: Router,
    app: Application
) : NoteAddOrEditViewModel(
    getCarItemUseCase,
    editCarItemUseCase,
    getNoteItemUseCase,
    deletePictureUseCase,
    router,
    app
) {

    var nTitle: String? by state(null)
    var nPrice: String? by state(null)
    var nMileage: String? by state(null)

    override val noteType = NoteType.REPAIR
    private var notesListForCalculation = mutableListOf<NoteItem>()

    init {
        withScope {
            notesListForCalculation = getNoteItemsListByMileageUseCase().toMutableList()
        }

        autorun(::noteItem) {
            if (it != null) {
                nTitle = it.title
                nPrice = getFormattedDoubleAsStr(it.totalPrice)
                nMileage = it.mileage.toString()
                nDate = it.date
            }
            else nDate = getCurrentDate()
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
            addNoteItemUseCase(newNote, pictures)
            notesListForCalculation.clear()
            notesListForCalculation.addAll(getNoteItemsListByMileageUseCase())

            calculateAllMileage()
            calculateAllPrice()
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
                    abs(max(notes.maxOf { it.mileage }, itCar.startMileage) - min(notes.minOf { it.mileage }, itCar.startMileage))
                else 0

            carItem = itCar.copy(
                allMileage = newMileage,
                mileage = max(itCar.startMileage, notes.first().mileage)
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

    private fun getNotExtraNotes(): List<NoteItem> {
        return notesListForCalculation.filter { it.type != NoteType.EXTRA }
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
