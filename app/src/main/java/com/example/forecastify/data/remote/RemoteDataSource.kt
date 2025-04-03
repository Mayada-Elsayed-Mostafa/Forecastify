package com.example.forecastify.data.remote

import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse

interface RemoteDataSource {

    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse
}