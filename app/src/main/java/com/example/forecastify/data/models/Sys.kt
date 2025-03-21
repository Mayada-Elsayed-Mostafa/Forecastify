package com.example.forecastify.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
) : Parcelable
