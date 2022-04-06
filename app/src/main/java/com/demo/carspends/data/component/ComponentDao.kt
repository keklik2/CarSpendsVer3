package com.demo.carspends.data.component

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ComponentDao {

    @Query("SELECT * FROM components ORDER BY date DESC")
    suspend fun getComponentsList(): List<ComponentItemDbModel>

    @Query("SELECT * FROM components WHERE id == :requestedId LIMIT 1")
    suspend fun getComponentById(requestedId: Int): ComponentItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(componentItemDbModel: ComponentItemDbModel)

    @Delete
    suspend fun deleteComponent(componentItemDbModel: ComponentItemDbModel)
}
