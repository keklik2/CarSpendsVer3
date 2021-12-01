package com.demo.carspends.data.car

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY id DESC")
    fun getCarsListLD(): LiveData<List<CarItemDbModel>>

    @Query("SELECT * FROM cars WHERE id == :requestedId LIMIT 1")
    fun getCarById(requestedId: Int): CarItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(carItemDbModel: CarItemDbModel)

    @Delete
    fun deleteCar(carItemDbModel: CarItemDbModel)
}