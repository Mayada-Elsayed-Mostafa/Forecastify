package com.example.forecastify.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alarmName: String,
    val selectedDate: String,
    val selectedTime: String,
    val lat: Double,
    val lon: Double
)
