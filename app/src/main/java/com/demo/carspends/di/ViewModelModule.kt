package com.demo.carspends.di

import androidx.lifecycle.ViewModel
import com.demo.carspends.presentation.fragments.carAddOrEditFragment.CarAddOrEditViewModel
import com.demo.carspends.presentation.fragments.componentAddOrEditFragment.ComponentAddOrEditViewModel
import com.demo.carspends.presentation.fragments.componentsListFragment.ComponentsListViewModel
import com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment.NoteExtraAddOrEditViewModel
import com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment.NoteFillingAddOrEditViewModel
import com.demo.carspends.presentation.fragments.noteRepairAddOrEditFragment.NoteRepairAddOrEditViewModel
import com.demo.carspends.presentation.fragments.notesListFragment.NotesListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CarAddOrEditViewModel::class)
    fun bindCarAddOrEditViewModel(viewModel: CarAddOrEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ComponentAddOrEditViewModel::class)
    fun bindComponentAddOrEditViewModel(viewModel: ComponentAddOrEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ComponentsListViewModel::class)
    fun bindComponentsListViewModel(viewModel: ComponentsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoteExtraAddOrEditViewModel::class)
    fun bindNoteExtraAddOrEditViewModel(viewModel: NoteExtraAddOrEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoteFillingAddOrEditViewModel::class)
    fun bindNoteFillingAddOrEditViewModel(viewModel: NoteFillingAddOrEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoteRepairAddOrEditViewModel::class)
    fun bindNoteRepairAddOrEditViewModel(viewModel: NoteRepairAddOrEditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotesListViewModel::class)
    fun bindNotesListViewModel(viewModel: NotesListViewModel): ViewModel

}