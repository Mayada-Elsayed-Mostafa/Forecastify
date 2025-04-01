package com.example.forecastify.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_location_table")
class FavoriteLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val address: String,
    val lat: Double,
    val lon: Double,
)