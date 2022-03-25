package com.demo.carspends.di

import android.app.Application
import com.demo.carspends.presentation.fragments.car.CarAddOrEditFragment
import com.demo.carspends.presentation.fragments.component.ComponentAddOrEditFragment
import com.demo.carspends.presentation.fragments.componentsList.ComponentsListFragment
import com.demo.carspends.presentation.fragments.noteExtra.NoteExtraAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteFilling.NoteFillingAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteRepair.NoteRepairAddOrEditFragment
import com.demo.carspends.presentation.fragments.notesList.NotesListFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

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