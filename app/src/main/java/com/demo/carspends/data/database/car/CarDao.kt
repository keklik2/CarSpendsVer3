package com.demo.carspends.data.database.car

import androidx.room.*

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY id DESC")
    suspend fun getCarsList(): List<CarItemDbModel>

    @Query("SELECT * FROM cars WHERE id == :requestedId LIMIT 1")
    suspend fun getCarById(requestedId: Int): CarItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(carItemDbModel: CarItemDbModel)

    @Delete
    suspend fun deleteCar(carItemDbModel: CarItemDbModel)
}
