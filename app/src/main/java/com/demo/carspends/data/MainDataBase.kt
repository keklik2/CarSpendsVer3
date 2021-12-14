package com.demo.carspends.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.demo.carspends.data.car.CarDao
import com.demo.carspends.data.car.CarItemDbModel
import com.demo.carspends.data.component.ComponentDao
import com.demo.carspends.data.component.ComponentItemDbModel
import com.demo.carspends.data.mapper.DbConverters
import com.demo.carspends.data.note.NoteDao
import com.demo.carspends.data.note.NoteItemDbModel

@Database(entities = [NoteItemDbModel::class, ComponentItemDbModel::class, CarItemDbModel::class], version = 10, exportSchema = false)
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
                        .addMigrations(MIGRATION_9_10)
                        .build()
                db = instance
                return instance
            }
        }

        private val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                with(database) {
                    execSQL("ALTER TABLE cars ADD COLUMN allFuel DOUBLE DEFAULT 0.0 NOT NULL")
                    execSQL("ALTER TABLE cars ADD COLUMN fuelPrice DOUBLE DEFAULT 0.0 NOT NULL")
                    execSQL("ALTER TABLE cars ADD COLUMN allPrice DOUBLE DEFAULT 0.0 NOT NULL")
                    execSQL("ALTER TABLE cars ADD COLUMN allMileage DOUBLE DEFAULT 0.0 NOT NULL")
                }
            }
        }
    }

    abstract fun noteDao(): NoteDao

    abstract fun componentDao(): ComponentDao

    abstract fun carDao(): CarDao
}