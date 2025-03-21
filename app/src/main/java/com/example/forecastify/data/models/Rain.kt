package com.example.forecastify.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Rain(
    val oneHour: Double,
) : Parcelable
