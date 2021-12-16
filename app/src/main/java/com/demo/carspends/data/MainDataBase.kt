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

@Database(entities = [NoteItemDbModel::class, ComponentItemDbModel::class, CarItemDbModel::class], version = 12, exportSchema = false)
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
                        .addMigrations(MIGRATION_9_10, MIGRATION_10_12)
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

        private val MIGRATION_10_12: Migration = object : Migration(10, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                with(database) {
                    database.execSQL(
                        "CREATE TABLE cars_new (id INTEGER NOT NULL, title TEXT NOT NULL, startMileage INTEGER NOT NULL, mileage INTEGER NOT NULL, engineVolume DOUBLE NOT NULL, power INTEGER NOT NULL, avgFuel DOUBLE NOT NULL, momentFuel DOUBLE NOT NULL, allFuel DOUBLE NOT NULL, fuelPrice DOUBLE NOT NULL, milPrice DOUBLE NOT NULL, allPrice DOUBLE NOT NULL, PRIMARY KEY(id))")
                    execSQL(
                                "INSERT INTO cars_new (id, title, startMileage, mileage, engineVolume, power, avgFuel, momentFuel, allFuel, fuelPrice, milPrice, allPrice) SELECT id, title, startMileage, mileage, engineVolume, power, avgFuel, momentFuel, allFuel, fuelPrice, milPrice, allPrice FROM cars")
                    execSQL("DROP TABLE cars");
                    execSQL("ALTER TABLE cars_new ADD COLUMN allMileage INTEGER DEFAULT 0 NOT NULL")
                    execSQL("ALTER TABLE cars_new RENAME TO cars")
                };
            }
        }
    }

    abstract fun noteDao(): NoteDao

    abstract fun componentDao(): ComponentDao

    abstract fun carDao(): CarDao
}