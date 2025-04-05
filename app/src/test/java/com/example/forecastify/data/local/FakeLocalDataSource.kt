package com.example.forecastify.data.local

import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class FakeLocalDataSource:LocalDataSource {

    private val locations = MutableStateFlow<List<FavoriteLocation>>(emptyList())

    override fun getWeatherFromRoom(): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override fun getForecastFromRoom(): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun insertForecast(forecastResponse: ForecastResponse) {
        TODO("Not yet implemented")
    }

    override fun getAllLocations(): Flow<List<FavoriteLocation>> {
        return locations.asStateFlow()
    }

    override suspend fun insertLocation(location: FavoriteLocation) {
        locations.update { current -> current + location }
    }

    override suspend fun deleteLocation(id: Int) {
        locations.update { current -> current.filterNot { it.id == id } }
    }

    override fun getAllAlarms(): Flow<List<AlarmItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alertItem: AlarmItem) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarmItem: AlarmItem) {
        TODO("Not yet implemented")
    }
}