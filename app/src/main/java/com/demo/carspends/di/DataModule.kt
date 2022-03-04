package com.demo.carspends.di

import android.app.Application
import com.demo.carspends.data.MainDataBase
import com.demo.carspends.data.car.CarDao
import com.demo.carspends.data.component.ComponentDao
import com.demo.carspends.data.note.NoteDao
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.ComponentRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.CarRepository
import com.demo.carspends.domain.component.ComponentRepository
import com.demo.carspends.domain.note.NoteRepository
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