package com.example.forecastify.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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
