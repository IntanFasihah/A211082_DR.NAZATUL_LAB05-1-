package com.example.a211082_drnazatul_lab05.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: PlaceEntity)

    @Delete
    suspend fun delete(place: PlaceEntity)

    @Query("SELECT * FROM places ORDER BY id DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>
}