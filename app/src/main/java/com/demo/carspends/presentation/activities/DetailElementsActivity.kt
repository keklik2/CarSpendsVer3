package com.demo.carspends.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.carspends.CarSpendsApp
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem.Companion.UNDEFINED_ID
import com.demo.carspends.presentation.fragments.car.CarAddOrEditFragment
import com.demo.carspends.presentation.fragments.component.ComponentAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteExtra.NoteExtraAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteFilling.NoteFillingAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteRepair.NoteRepairAddOrEditFragment
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class DetailElementsActivity : AppCompatActivity() {

    private lateinit var launchMode: String
    private var itemId = UNDEFINED_ID
    private var carId = CarItem.UNDEFINED_ID

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private val navigator = AppNavigator(this, R.id.detail_activity_fragment_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CarSpendsApp).component.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_elements)
        receiveIntent()

        if (savedInstanceState == null) {
            val fragment = when(launchMode) {
                EXTRA_NOTE_ADD -> NoteExtraAddOrEditFragment.newAddInstance(carId)
                EXTRA_NOTE_EDIT -> NoteExtraAddOrEditFragment.newEditInstance(carId, itemId)
                FILLING_NOTE_ADD -> NoteFillingAddOrEditFragment.newAddInstance(carId)
                FILLING_NOTE_EDIT -> NoteFillingAddOrEditFragment.newEditInstance(carId, itemId)
                REPAIR_NOTE_ADD -> NoteRepairAddOrEditFragment.newAddInstance(carId)
                REPAIR_NOTE_EDIT -> NoteRepairAddOrEditFragment.newEditInstance(carId, itemId)
                COMPONENT_ADD -> ComponentAddOrEditFragment.newAddInstance(carId)
                COMPONENT_EDIT -> ComponentAddOrEditFragment.newEditInstance(carId, itemId)
                CAR_ADD -> CarAddOrEditFragment.newAddInstance()
                CAR_EDIT -> CarAddOrEditFragment.newEditInstance(itemId)
                else -> throw Exception("Unknown KEY_MODE for DetailElementsActivity: $launchMode")
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_activity_fragment_container, fragment)
                .commit()
        }

    }

    private fun receiveIntent() {
        if (!intent.hasExtra(KEY_MODE)) {
            throw Exception("Starting DetailElementsActivity requires KEY_MODE parameter with intent")
        }

        val keyType = intent.getStringExtra(KEY_MODE)
        if (keyType != EXTRA_NOTE_EDIT
            && keyType != EXTRA_NOTE_ADD
            && keyType != FILLING_NOTE_ADD
            && keyType != FILLING_NOTE_EDIT
            && keyType != REPAIR_NOTE_ADD
            && keyType != REPAIR_NOTE_EDIT
            && keyType != COMPONENT_ADD
            && keyType != COMPONENT_EDIT
            && keyType != CAR_ADD
            && keyType != CAR_EDIT) {
            throw Exception("Unknown type for DetailElementsActivity. Received: $keyType")
        }

        launchMode = keyType
        if (keyType == EXTRA_NOTE_EDIT || keyType == FILLING_NOTE_EDIT || keyType == REPAIR_NOTE_EDIT || keyType == COMPONENT_EDIT || keyType == CAR_EDIT) {
            if (!intent.hasExtra(KEY_NOTE_ID)) {
                throw Exception("$keyType requires KEY_NOTE_ID param with intent for DetailElementsActivity")
            }

            val id = intent.getIntExtra(KEY_NOTE_ID, UNDEFINED_ID)
            if (id == UNDEFINED_ID) {
                throw Exception("Item with id $id cannot exist")
            }
            itemId = id
        }

        if (keyType != CAR_EDIT && keyType != CAR_ADD) {
            if (!intent.hasExtra(KEY_CAR_ID)) throw Exception("..._NOTE_EDIT requires KEY_CAR_ID param with intent for DetailElementsActivity")
            val cId = intent.getIntExtra(KEY_CAR_ID, CarItem.UNDEFINED_ID)
            if (cId == CarItem.UNDEFINED_ID) {
                throw Exception("CarItem with id $cId cannot exist")
            }
            carId = cId
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }


    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    companion object {
        private const val KEY_MODE = "key_mode"
        private const val KEY_NOTE_ID = "key_note"
        private const val KEY_CAR_ID = "key_car"

        private const val EXTRA_NOTE_EDIT = "extra_note_edit"
        private const val EXTRA_NOTE_ADD = "extra_note_add"
        private const val FILLING_NOTE_EDIT = "filling_note_edit"
        private const val FILLING_NOTE_ADD = "filling_note_add"
        private const val REPAIR_NOTE_EDIT = "repair_note_edit"
        private const val REPAIR_NOTE_ADD = "repair_note_add"
        private const val COMPONENT_EDIT = "component_edit"
        private const val COMPONENT_ADD = "component_add"
        private const val CAR_EDIT = "car_edit"
        private const val CAR_ADD = "car_add"

        fun newAddOrEditNoteExtraIntent(context: Context, carId: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, EXTRA_NOTE_ADD)
            intent.putExtra(KEY_CAR_ID, carId)
            return intent
        }

        fun newAddOrEditNoteExtraIntent(context: Context, carId: Int, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, EXTRA_NOTE_EDIT)
            intent.putExtra(KEY_CAR_ID, carId)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditNoteFillingIntent(context: Context, carId: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, FILLING_NOTE_ADD)
            intent.putExtra(KEY_CAR_ID, carId)
            return intent
        }

        fun newAddOrEditNoteFillingIntent(context: Context, carId: Int, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, FILLING_NOTE_EDIT)
            intent.putExtra(KEY_CAR_ID, carId)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditNoteRepairIntent(context: Context, carId: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, REPAIR_NOTE_ADD)
            intent.putExtra(KEY_CAR_ID, carId)
            return intent
        }

        fun newAddOrEditNoteRepairIntent(context: Context, carId: Int, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, REPAIR_NOTE_EDIT)
            intent.putExtra(KEY_CAR_ID, carId)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditComponentIntent(context: Context, carId: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, COMPONENT_ADD)
            intent.putExtra(KEY_CAR_ID, carId)
            return intent
        }

        fun newAddOrEditComponentIntent(context: Context, carId: Int, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, COMPONENT_EDIT)
            intent.putExtra(KEY_CAR_ID, carId)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditCarIntent(context: Context): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, CAR_ADD)
            return intent
        }

        fun newAddOrEditCarIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, CAR_EDIT)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }
    }
}
