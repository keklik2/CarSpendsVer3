package com.demo.carspends.utils.ui.baseViewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.picture.DeletePictureUseCase
import com.demo.carspends.domain.picture.InternalPicture
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state

abstract class NoteAddOrEditViewModel(
    private val getCarItemUseCase: GetCarItemUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val getNoteItemUseCase: GetNoteItemUseCase,
    private val deletePictureUseCase: DeletePictureUseCase,
    private val router: Router,
    private val app: Application
): BaseViewModel(app)  {
    fun goBack() = router.exit()

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope

    abstract val noteType: NoteType
    var noteId: Int? by state(null)
    var noteItem: NoteItem? by state(null)
    var carId: Int? by state(null)
    var carItem: CarItem? by state(null)
    var pictures by state(mutableListOf<InternalPicture>())
    var nDate by state(getCurrentDate())

    var canCloseScreen by state(false)

    init {
        autorun(::noteId) {
            withScope {
                if (it != null && it > 0) {
                    withScope {
                        val map = getNoteItemUseCase(it)
                        noteItem = map.keys.firstOrNull()
                        pictures =
                            if(noteItem != null) map[noteItem]?.toMutableList() ?: mutableListOf()
                            else mutableListOf()
                    }
                }
                else {
                    noteItem = null
                    nDate = getCurrentDate()
                }
            }
        }

        autorun(::carId) {
            withScope {
                if (it != null) {
                    withScope {
                        carItem = getCarItemUseCase(it)
                    }
                }
                else carItem = null
            }
        }
    }

    fun addPictures(newPictures: List<InternalPicture>) {
        pictures = pictures.toMutableList().apply {
            addAll(newPictures)
        }
        if(pictures.size > 4) pictures = pictures.dropLast(pictures.size - 4).toMutableList()
    }

    fun deletePicture(picture: InternalPicture) {
        pictures.firstOrNull {
            it.name == picture.name && it.uri == picture.uri
        }?.let { itPicture ->
            pictures = pictures.toMutableList().apply {
                remove(itPicture)
            }
            noteId?.let { itNoteId ->
                withScope {
                    deletePictureUseCase(itNoteId, itPicture)
                }
            }
        }
    }

    fun setCanCloseScreen() { canCloseScreen = true }
    suspend fun updateCarItem() = carItem?.let { editCarItemUseCase(it) }
}
