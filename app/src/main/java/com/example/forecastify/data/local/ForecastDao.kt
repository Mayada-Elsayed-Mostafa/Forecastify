package com.example.forecastify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastify.data.models.ForecastResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastResponse)

    @Query("SELECT * FROM forecastresponsetable")
    fun getForecast(): Flow<ForecastResponse>

    @Query("DELETE FROM forecastresponsetable")
    suspend fun deleteAllForecastData()
}
