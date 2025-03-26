package com.example.forecastify.data.remote

import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RemoteDataSourceImp(private val service: WeatherService) : RemoteDataSource {

    val apiKey = "65f5ae26a23ee25d8e12c2afeb2a72a3"

    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse? {
        return service.getCurrentWeather(lat, lon, apiKey).body()
    }

    override suspend fun getForecastOfDay(lat: Double, lon: Double): ForecastResponse? {
        return try {
            val response = service.getForecastOfDay(lat, lon, apiKey)
            if (response.isSuccessful) {
                val forecastResponse = response.body()

                val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val todayForecasts =
                    forecastResponse?.list?.filter { it.dt_txt.startsWith(todayDate) }

                forecastResponse?.copy(list = todayForecasts ?: emptyList())
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }
    }

}
