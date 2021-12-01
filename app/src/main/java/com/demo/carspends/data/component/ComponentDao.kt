package com.demo.carspends.data.component

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ComponentDao {

    @Query("SELECT * FROM components ORDER BY date DESC")
    fun getComponentsListLD(): LiveData<List<ComponentItemDbModel>>

    @Query("SELECT * FROM components WHERE id == :requestedId LIMIT 1")
    fun getComponentById(requestedId: Int): ComponentItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponent(componentItemDbModel: ComponentItemDbModel)

    @Delete
    fun deleteComponent(componentItemDbModel: ComponentItemDbModel)
}