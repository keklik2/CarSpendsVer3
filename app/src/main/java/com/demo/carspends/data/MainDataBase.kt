package com.demo.carspends.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.carspends.data.car.CarDao
import com.demo.carspends.data.car.CarItemDbModel
import com.demo.carspends.data.component.ComponentDao
import com.demo.carspends.data.component.ComponentItemDbModel
import com.demo.carspends.data.mapper.DbConverters
import com.demo.carspends.data.note.NoteDao
import com.demo.carspends.data.note.NoteItemDbModel

@Database(entities = [NoteItemDbModel::class, ComponentItemDbModel::class, CarItemDbModel::class], version = 9, exportSchema = false)
@TypeConverters(DbConverters::class)
abstract class MainDataBase: RoomDatabase() {
    companion object {
        private var db: MainDataBase? = null

        private val LOCK = Any()
        private const val DB_NAME = "main.db"

        fun getInstance(context: Context): MainDataBase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        MainDataBase::class.java,
                        DB_NAME
                    )
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun noteDao(): NoteDao

    abstract fun componentDao(): ComponentDao

    abstract fun carDao(): CarDao
}