package com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class NoteExtraAddOrEditViewModel(app: Application): AndroidViewModel(app) {

    private val repository = NoteRepositoryImpl(app)

    private val noteType = NoteType.EXTRA

    private val addNoteItemUseCase = AddNoteItemUseCase(repository)
    private val editNoteItemUseCase = EditNoteItemUseCase(repository)
    private val getNoteItem = GetNoteItemUseCase(repository)

    private val _noteDate = MutableLiveData<Long>()
    val noteDate get() = _noteDate

    private val _errorPriceInput = MutableLiveData<Boolean>()
    val errorPriceInput get() = _errorPriceInput

    private val _errorTitleInput = MutableLiveData<Boolean>()
    val errorTitleInput get() = _errorTitleInput

    private val _noteItem = MutableLiveData<NoteItem>()
    val noteItem get() = _noteItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _noteDate.value = Date().time
    }

    fun addNoteItem(title: String?, price: String?) {
        val rTitle = refactorTitle(title)
        val rPrice = refactorPrice(price)

        if(areFieldsValid(rTitle, rPrice)) {
            viewModelScope.launch {
                val nDate = _noteDate.value
                if (nDate != null) {
                    addNoteItemUseCase(
                        NoteItem(
                            title = rTitle,
                            totalPrice = rPrice,
                            date = nDate,
                            type = noteType
                        )
                    )
                    setCanCloseScreen()
                } else Exception("Received NULL NoteItem for AddNoteItemUseCase()")
            }
        }
    }

    fun editNoteItem(title: String?, price: String?) {
        val rTitle = refactorTitle(title)
        val rPrice = refactorPrice(price)

        if(areFieldsValid(rTitle, rPrice)) {
            viewModelScope.launch {
                val nItem = _noteItem.value
                if (nItem != null) {
                    val nDate = _noteDate.value
                    if (nDate != null) {
                        editNoteItemUseCase(
                            nItem.copy(
                                title = rTitle,
                                totalPrice = rPrice,
                                date = nDate,
                                type = noteType
                            )
                        )
                        setCanCloseScreen()
                    } else Exception("Received NULL NoteDate for AddNoteItemUseCase()")
                } else Exception("Received NULL NoteItem for EditNoteItemUseCase()")
            }
        }
    }

    private fun refactorTitle(title: String?): String {
        return title?.trim() ?: ""
    }

    private fun refactorPrice(price: String?): Double {
        return try {
            price?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun areFieldsValid(title: String, price: Double): Boolean {
        if (title.isBlank()) {
            _errorTitleInput.value = true
            return false
        }
        if (price <= 0.0) {
            _errorPriceInput.value = true
            return false
        }
        return true
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getNoteItem(id)
            _noteItem.value = item
            _noteDate.value = item.date
        }
    }

    fun resetTitleError() {
        _errorTitleInput.value = false
    }

    fun resetPriceError() {
        _errorPriceInput.value = false
    }

    fun setNoteDate(date: Long) {
        _noteDate.value = date
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }
}