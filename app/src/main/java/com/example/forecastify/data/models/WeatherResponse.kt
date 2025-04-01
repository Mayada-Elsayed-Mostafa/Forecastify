package com.example.forecastify.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Entity(tableName = "weatherresponsetable")
@Parcelize
data class WeatherResponse(
    @PrimaryKey
    val id: Int,

    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain?,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val name: String,
    val cod: Int,
) : Parcelable

@Parcelize
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double,
) : Parcelable

@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
) : Parcelable

@Parcelize
data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
) : Parcelable

@Parcelize
data class Rain(
    val oneHour: Double,
) : Parcelable

@Parcelize
data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val seaLevel: Int,
    val grndLevel: Int,
) : Parcelable

@Parcelize
data class Coord(
    val lon: Double,
    val lat: Double,
) : Parcelable

@Parcelize
data class Clouds(
    val all: Int,
) : Parcelable
