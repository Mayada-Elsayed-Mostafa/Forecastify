package com.example.forecastify.data.local

import androidx.room.TypeConverter
import com.example.forecastify.data.models.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherResponse(weather: WeatherResponse?): String {
        return gson.toJson(weather)
    }

    @TypeConverter
    fun toWeatherResponse(data: String): WeatherResponse? {
        val type = object : TypeToken<WeatherResponse>() {}.type
        return gson.fromJson(data, type)
    }
}
