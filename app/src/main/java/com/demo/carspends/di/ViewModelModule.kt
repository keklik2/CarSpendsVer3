package com.demo.carspends.di

import androidx.lifecycle.ViewModel
import com.demo.carspends.presentation.fragments.car.CarAddOrEditViewModel
import com.demo.carspends.presentation.fragments.component.ComponentAddOrEditViewModel
import com.demo.carspends.presentation.fragments.componentsList.ComponentsListViewModel
import com.demo.carspends.presentation.fragments.noteExtra.NoteExtraAddOrEditViewModel
import com.demo.carspends.presentation.fragments.noteFilling.NoteFillingAddOrEditViewModel
import com.demo.carspends.presentation.fragments.noteRepair.NoteRepairAddOrEditViewModel
import com.demo.carspends.presentation.fragments.notesList.NotesListViewModel
import com.demo.carspends.presentation.fragments.settings.SettingsViewModel
import com.demo.carspends.presentation.fragments.statistics.StatisticsFragment
import com.demo.carspends.presentation.fragments.statistics.StatisticsViewModel
import com.demo.carspends.presentation.fragments.statistics.graphics.GraphicsViewModel
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

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel::class)
    fun bindStatisticsViewModel(viewModel: StatisticsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GraphicsViewModel::class)
    fun bindGraphicsViewModel(viewModel: GraphicsViewModel): ViewModel

}
