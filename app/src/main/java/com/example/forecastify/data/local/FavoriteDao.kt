package com.example.forecastify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastify.data.models.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FavoriteLocation)

    @Query("SELECT * FROM favorite_location_table")
    fun getAllLocations(): Flow<List<FavoriteLocation>>

    @Query("DELETE FROM favorite_location_table WHERE id = :id")
    suspend fun deleteLocation(id: Int)
}
