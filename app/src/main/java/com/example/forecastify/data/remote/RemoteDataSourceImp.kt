package com.example.forecastify.data.remote

import com.example.forecastify.data.models.WeatherResponse

class RemoteDataSourceImp(private val service: WeatherService) : RemoteDataSource {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse? {
        val apiKey = "65f5ae26a23ee25d8e12c2afeb2a72a3"
        return service.getCurrentWeather(lat, lon, apiKey).body()
    }
}
