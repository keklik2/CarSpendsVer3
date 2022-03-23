package com.demo.carspends.di

import android.app.Application
import androidx.fragment.app.Fragment
import com.demo.carspends.presentation.activities.MainActivity
import com.demo.carspends.presentation.fragments.carAddOrEditFragment.CarAddOrEditFragment
import com.demo.carspends.presentation.fragments.componentAddOrEditFragment.ComponentAddOrEditFragment
import com.demo.carspends.presentation.fragments.componentsListFragment.ComponentsListFragment
import com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment.NoteExtraAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment.NoteFillingAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteRepairAddOrEditFragment.NoteRepairAddOrEditFragment
import com.demo.carspends.presentation.fragments.notesListFragment.NotesListFragment
import com.github.terrakok.cicerone.Router
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class,
        NavigationModule::class
    ]
)
interface ApplicationComponent {
    fun provideRouter(): Router

    fun inject(activity: MainActivity)

    fun inject(fragment: CarAddOrEditFragment)
    fun inject(fragment: ComponentAddOrEditFragment)
    fun inject(fragment: ComponentsListFragment)
    fun inject(fragment: NoteExtraAddOrEditFragment)
    fun inject(fragment: NoteFillingAddOrEditFragment)
    fun inject(fragment: NoteRepairAddOrEditFragment)
    fun inject(fragment: NotesListFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}