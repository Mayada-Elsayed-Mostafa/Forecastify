package com.example.forecastify.remote

import com.example.forecastify.models.WeatherResponse

class RemoteDataSourceImp(private val service: WeatherService) : RemoteDataSource {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse? {

        val apiKey = "41a9063d5c8ae577749870ab1b9880b8"
        return service.getCurrentWeather(lat, lon, apiKey).body()
    }
}
