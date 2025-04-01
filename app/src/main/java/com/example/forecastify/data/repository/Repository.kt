package com.example.forecastify.data.repository

import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.MainOfForecast
import com.example.forecastify.data.models.WeatherResponse

interface WeatherRepository {

    suspend fun getWeather(isOnline: Boolean, lat: Double, lon: Double): WeatherResponse?

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse?

    suspend fun getUpcomingForecast(lat: Double, lon: Double): ForecastResponse?

    //suspend fun addWeather(weather: WeatherResponse): Long

    //suspend fun removeWeather(weather: WeatherResponse): Int

}
