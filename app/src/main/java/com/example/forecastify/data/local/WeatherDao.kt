package com.example.forecastify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherResponse)

    @Query("SELECT * FROM weatherresponsetable LIMIT 1")
    fun getWeather(): Flow<WeatherResponse>

    @Query("DELETE FROM weatherresponsetable")
    suspend fun deleteAllWeatherData()
}
