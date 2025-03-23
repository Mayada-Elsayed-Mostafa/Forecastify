package com.example.forecastify.data.local

import androidx.room.TypeConverter
import com.example.forecastify.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromCoord(coord: Coord): String {
        return gson.toJson(coord)
    }

    @TypeConverter
    fun toCoord(data: String): Coord {
        val type = object : TypeToken<Coord>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromWeatherList(weather: List<Weather>): String {
        return gson.toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(data: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromMain(main: Main): String {
        return gson.toJson(main)
    }

    @TypeConverter
    fun toMain(data: String): Main {
        val type = object : TypeToken<Main>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromWind(wind: Wind): String {
        return gson.toJson(wind)
    }

    @TypeConverter
    fun toWind(data: String): Wind {
        val type = object : TypeToken<Wind>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromRain(rain: Rain?): String? {
        return gson.toJson(rain)
    }

    @TypeConverter
    fun toRain(data: String?): Rain? {
        val type = object : TypeToken<Rain?>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromClouds(clouds: Clouds): String {
        return gson.toJson(clouds)
    }

    @TypeConverter
    fun toClouds(data: String): Clouds {
        val type = object : TypeToken<Clouds>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromSys(sys: Sys): String {
        return gson.toJson(sys)
    }

    @TypeConverter
    fun toSys(data: String): Sys {
        val type = object : TypeToken<Sys>() {}.type
        return gson.fromJson(data, type)
    }
}
