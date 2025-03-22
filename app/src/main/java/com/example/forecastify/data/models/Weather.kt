package com.example.forecastify.data.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize


@Entity(tableName = "weathertable")
@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
) : Parcelable
