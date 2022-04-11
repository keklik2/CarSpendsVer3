package com.demo.carspends.di

import android.app.Application
import com.demo.carspends.data.database.MainDataBase
import com.demo.carspends.data.database.car.CarDao
import com.demo.carspends.data.database.component.ComponentDao
import com.demo.carspends.data.database.note.NoteDao
import com.demo.carspends.data.database.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.database.repositoryImpls.ComponentRepositoryImpl
import com.demo.carspends.data.database.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.data.settings.SettingsRepositoryImpl
import com.demo.carspends.domain.car.CarRepository
import com.demo.carspends.domain.component.ComponentRepository
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.settings.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    fun bindCarRepository(impl: CarRepositoryImpl): CarRepository

    @Binds
    fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    fun bindComponentRepository(impl: ComponentRepositoryImpl): ComponentRepository

    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {

        @Provides
        fun provideCarDao(application: Application): CarDao {
            return MainDataBase.getInstance(application).carDao()
        }

        @Provides
        fun provideComponentDao(application: Application): ComponentDao {
            return MainDataBase.getInstance(application).componentDao()
        }

        @Provides
        fun provideNoteDao(application: Application): NoteDao {
            return MainDataBase.getInstance(application).noteDao()
        }

    }

}
