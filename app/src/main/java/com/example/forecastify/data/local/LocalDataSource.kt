package com.example.forecastify.data.local

import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getWeatherFromRoom(): Flow<WeatherResponse>

    suspend fun insertWeather(weatherResponse: WeatherResponse)

    fun getForecastFromRoom(): Flow<ForecastResponse>

    suspend fun insertForecast(forecastResponse: ForecastResponse)

    fun getAllLocations(): Flow<List<FavoriteLocation>>

    suspend fun insertLocation(location: FavoriteLocation)

    suspend fun deleteLocation(location: FavoriteLocation)

    fun getAllAlarms(): Flow<List<AlarmItem>>

    suspend fun insertAlarm(alertItem: AlarmItem)

    suspend fun deleteAlarm(alarmItem: AlarmItem)

    suspend fun deleteAlarmTime(time: String)
}
