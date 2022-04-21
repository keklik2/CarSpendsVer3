package com.demo.carspends.data.database.component

import androidx.room.*

@Dao
interface ComponentDao {

    @Query("SELECT * FROM components ORDER BY date DESC")
    suspend fun getComponents(): List<ComponentItemDbModel>

    @Query("SELECT * FROM components WHERE id == :requestedId LIMIT 1")
    suspend fun getComponent(requestedId: Int): ComponentItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(componentItemDbModel: ComponentItemDbModel)

    @Delete
    suspend fun delete(componentItemDbModel: ComponentItemDbModel)
}
