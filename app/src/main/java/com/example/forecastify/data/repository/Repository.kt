package com.example.forecastify.data.repository

import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getWeather(isOnline: Boolean, lat: Double, lon: Double): Flow<WeatherResponse>

    suspend fun getUpcomingForecast(
        isOnline: Boolean,
        lat: Double,
        lon: Double,
    ): Flow<ForecastResponse>

    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>

    suspend fun addLocation(favoriteLocation: FavoriteLocation)

    suspend fun deleteLocation(location: FavoriteLocation)

    fun getAllAlarms(): Flow<List<AlarmItem>>

    suspend fun addAlarm(alarmItem: AlarmItem)

    suspend fun removeAlarm(alarmItem: AlarmItem)

    suspend fun deleteAlarmTime(time: String)
}
