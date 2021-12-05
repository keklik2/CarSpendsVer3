package com.demo.carspends.presentation.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.carspends.R
import com.demo.carspends.domain.note.NoteItem.Companion.UNDEFINED_ID
import com.demo.carspends.presentation.fragments.OnEditingFinishedListener
import com.demo.carspends.presentation.fragments.componentAddOrEditFragment.ComponentAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment.NoteExtraAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment.NoteFillingAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteRepairAddOrEditFragment.NoteRepairAddOrEditFragment
import java.lang.Exception

class DetailElementsActivity : AppCompatActivity(), OnEditingFinishedListener {

    private lateinit var launchMode: String
    private var itemId = UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_elements)

        receiveIntent()

        if (savedInstanceState == null) {
            val fragment = when(launchMode) {
                EXTRA_NOTE_ADD -> NoteExtraAddOrEditFragment.newAddInstance()
                EXTRA_NOTE_EDIT -> NoteExtraAddOrEditFragment.newEditInstance(itemId)
                FILLING_NOTE_ADD -> NoteFillingAddOrEditFragment.newAddInstance()
                FILLING_NOTE_EDIT -> NoteFillingAddOrEditFragment.newEditInstance(itemId)
                REPAIR_NOTE_ADD -> NoteRepairAddOrEditFragment.newAddInstance()
                REPAIR_NOTE_EDIT -> NoteRepairAddOrEditFragment.newEditInstance(itemId)
                COMPONENT_ADD -> ComponentAddOrEditFragment.newAddInstance()
                COMPONENT_EDIT -> ComponentAddOrEditFragment.newEditInstance(itemId)
                else -> throw Exception("Unknown KEY_MODE for DetailElementsActivity")
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
            && keyType != COMPONENT_EDIT) {
            throw Exception("Unknown type for DetailElementsActivity. Received: $keyType")
        }

        launchMode = keyType
        if (keyType == EXTRA_NOTE_EDIT || keyType == FILLING_NOTE_EDIT || keyType == REPAIR_NOTE_EDIT || keyType == COMPONENT_EDIT) {
            if (!intent.hasExtra(KEY_NOTE_ID)) {
                throw Exception("..._NOTE_EDIT requires KEY_NOTE_ID param with intent for DetailElementsActivity")
            }

            val id = intent.getIntExtra(KEY_NOTE_ID, UNDEFINED_ID)
            if (id == UNDEFINED_ID) {
                throw Exception("Item with id $id cannot exist")
            }
            itemId = id
        }
    }

    companion object {
        private const val KEY_MODE = "key_mode"
        private const val KEY_NOTE_ID = "key_note"

        private const val EXTRA_NOTE_EDIT = "extra_note_edit"
        private const val EXTRA_NOTE_ADD = "extra_note_add"
        private const val FILLING_NOTE_EDIT = "filling_note_edit"
        private const val FILLING_NOTE_ADD = "filling_note_add"
        private const val REPAIR_NOTE_EDIT = "repair_note_edit"
        private const val REPAIR_NOTE_ADD = "repair_note_add"
        private const val COMPONENT_EDIT = "component_edit"
        private const val COMPONENT_ADD = "component_add"

        fun newAddOrEditNoteExtraIntent(context: Context): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, EXTRA_NOTE_ADD)
            return intent
        }

        fun newAddOrEditNoteExtraIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, EXTRA_NOTE_EDIT)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditNoteFillingIntent(context: Context): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, FILLING_NOTE_ADD)
            return intent
        }

        fun newAddOrEditNoteFillingIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, FILLING_NOTE_EDIT)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditNoteRepairIntent(context: Context): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, REPAIR_NOTE_ADD)
            return intent
        }

        fun newAddOrEditNoteRepairIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, REPAIR_NOTE_EDIT)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }

        fun newAddOrEditComponentIntent(context: Context): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, COMPONENT_ADD)
            return intent
        }

        fun newAddOrEditComponentIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, DetailElementsActivity::class.java)
            intent.putExtra(KEY_MODE, COMPONENT_EDIT)
            intent.putExtra(KEY_NOTE_ID, id)
            return intent
        }
    }

    override fun onFinish() {
        finish()
    }
}