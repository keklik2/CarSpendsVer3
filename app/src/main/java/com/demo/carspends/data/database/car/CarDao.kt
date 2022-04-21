package com.demo.carspends.data.database.car

import androidx.room.*

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY id DESC")
    suspend fun getCars(): List<CarItemDbModel>

    @Query("SELECT * FROM cars WHERE id == :requestedId LIMIT 1")
    suspend fun getCar(requestedId: Int): CarItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carItemDbModel: CarItemDbModel)

    @Delete
    suspend fun delete(carItemDbModel: CarItemDbModel)
}
