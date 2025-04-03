package com.example.forecastify.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.forecastify.data.models.AlarmItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<AlarmItem>>

    @Insert
    suspend fun insertAlarm(alarm: AlarmItem): Long

    @Delete
    suspend fun deleteAlarm(alarm: AlarmItem): Int

    @Query("DELETE FROM alarms WHERE  selectedTime = :time" )
    suspend fun deleteAlarmTime(time: String): Int
}
