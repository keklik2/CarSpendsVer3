package com.demo.carspends

import android.content.Intent
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.activities.MainActivity
import com.demo.carspends.presentation.activities.SettingsActivity
import com.demo.carspends.presentation.fragments.componentsList.ComponentsListFragment
import com.demo.carspends.presentation.fragments.notesList.NotesListFragment
import com.demo.carspends.presentation.fragments.statistics.StatisticsFragment
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun CarEditOrAdd() = ActivityScreen {
        DetailElementsActivity.newAddOrEditCarIntent(it)
    }

    fun CarEditOrAdd(carId: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditCarIntent(it, carId)
    }

    fun ComponentEditOrAdd(carId: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditComponentIntent(it, carId)
    }

    fun ComponentEditOrAdd(carId: Int, id: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditComponentIntent(it, carId, id)
    }

    fun NoteExtra(carId: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditNoteExtraIntent(it, carId)
    }

    fun NoteExtra(carId: Int, id: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditNoteExtraIntent(it, carId, id)
    }

    fun NoteFilling(carId: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditNoteFillingIntent(it, carId)
    }

    fun NoteFilling(carId: Int, id: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditNoteFillingIntent(it, carId, id)
    }

    fun NoteRepair(carId: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditNoteRepairIntent(it, carId)
    }

    fun NoteRepair(carId: Int, id: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditNoteRepairIntent(it, carId, id)
    }

    fun NotesList() = FragmentScreen { NotesListFragment() }
    fun ComponentsList() = FragmentScreen { ComponentsListFragment() }
    fun Statistics() = FragmentScreen { StatisticsFragment() }

    fun Settings() = ActivityScreen {
        SettingsActivity.getInstance(it)
    }

    fun HomePage() = ActivityScreen { Intent(it, MainActivity::class.java) }
}
