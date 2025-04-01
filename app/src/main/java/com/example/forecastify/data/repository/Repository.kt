package com.example.forecastify.data.repository

import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getWeather(isOnline: Boolean, lat: Double, lon: Double): WeatherResponse?

    suspend fun getUpcomingForecast(lat: Double, lon: Double): ForecastResponse?

    fun getFavoriteLocations(): Flow<List<FavoriteLocation>>

    suspend fun addLocation(favoriteLocation: FavoriteLocation)

    suspend fun deleteLocation(id: Int)

}
