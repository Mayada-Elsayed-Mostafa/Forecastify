package com.example.forecastify.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
) : Parcelable
