package com.example.forecastify.data.local

import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun getWeatherFromRoom(): Flow<WeatherResponse>

    suspend fun insertWeather(weatherResponse: WeatherResponse)

    suspend fun getForecastFromRoom(): Flow<ForecastResponse>

    suspend fun insertForecast(forecastResponse: ForecastResponse)

    fun getAllLocations(): Flow<List<FavoriteLocation>>

    suspend fun insertLocation(location: FavoriteLocation)

    suspend fun deleteLocation(id: Int)

    suspend fun getAllAlarms(): Flow<List<AlarmItem>>

    suspend fun insertAlarm(alertItem: AlarmItem)

    suspend fun deleteAlarm(alarmItem: AlarmItem)
}
