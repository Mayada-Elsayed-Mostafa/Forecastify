package com.example.forecastify.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "forecastresponsetable")
@Parcelize
data class ForecastResponse(
    @PrimaryKey val id: Int,
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: CityOfForecast,
) : Parcelable

@Parcelize
data class WeatherItem(
    val dt: Long,
    val main: MainOfForecast,
    val weather: List<WeatherOfForecast>,
    val clouds: CloudsOfForecast,
    val wind: WindOfForecast,
    val visibility: Int,
    val pop: Double,
    val rain: RainOfForecast?,
    val sys: SysOfForecast,
    val dt_txt: String,
) : Parcelable

@Parcelize
data class CityOfForecast(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long,
) : Parcelable

@Parcelize
data class MainOfForecast(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double,
) : Parcelable

@Parcelize
data class WeatherOfForecast(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
) : Parcelable

@Parcelize
data class CloudsOfForecast(
    val all: Int,
) : Parcelable

@Parcelize
data class WindOfForecast(
    val speed: Double,
    val deg: Int,
    val gust: Double,
) : Parcelable

@Parcelize
data class RainOfForecast(
    val `3h`: Double,
) : Parcelable

@Parcelize
data class SysOfForecast(
    val pod: String,
) : Parcelable
