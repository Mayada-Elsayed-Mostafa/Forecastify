package com.example.forecastify.data.remote

import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse

class RemoteDataSourceImp(private val service: WeatherService) : RemoteDataSource {

    val apiKey = "65f5ae26a23ee25d8e12c2afeb2a72a3"

    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        return service.getCurrentWeather(lat, lon, apiKey).body()!!
    }

    override suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return service.getForecast(lat, lon, apiKey).body()!!
    }

}
