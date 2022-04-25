package com.demo.carspends.presentation.fragments.noteExtra

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.*
import com.demo.carspends.domain.picture.DeletePictureUseCase
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorString
import com.demo.carspends.utils.ui.baseViewModel.NoteAddOrEditViewModel
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.max


class NoteExtraAddOrEditViewModel @Inject constructor(
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val addNoteItemUseCase: AddNoteItemUseCase,
    getNoteItemUseCase: GetNoteItemUseCase,
    getCarItemUseCase: GetCarItemUseCase,
    editCarItemUseCase: EditCarItemUseCase,
    deletePictureUseCase: DeletePictureUseCase,
    router: Router,
    private val app: Application
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

    override val noteType = NoteType.EXTRA

    init {
        autorun(::noteItem) {
            withScope {
                if (it != null) {
                    nTitle = it.title
                    nPrice = it.totalPrice.toString()
                    nDate = it.date
                } else nDate = getCurrentDate()
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

        withScope {
            addNoteItemUseCase(newNote, pictures)

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
                    itList.sumOf { it1 -> it1.totalPrice },
                    0.0
                )
                carItem = itCar.copy(
                    allPrice = newAllPrice
                )
            }
        }
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
