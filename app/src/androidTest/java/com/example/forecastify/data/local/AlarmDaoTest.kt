package com.example.forecastify.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.forecastify.data.models.AlarmItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmDaoTest {

    lateinit var dao: AlarmDao
    lateinit var database: AppDatabase

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()
        dao = database.getAlarmDao()
    }

    @After
    fun close() = database.close()

    @Test
    fun insertAlarm_addsAlarm() = runBlocking {
        val alarm = AlarmItem(
            selectedTime = "08:00",
            alarmName = "Morning Alarm",
            lon = 33.1,
            lat = 31.1,
            selectedDate = "09/09/2002"
        )

        val alarmId = dao.insertAlarm(alarm)

        val allAlarms = dao.getAllAlarms().first()
        assertEquals(1, allAlarms.size)
        assertEquals(alarm.selectedTime, allAlarms[0].selectedTime)
    }

    @Test
    fun deleteAlarmTime_removesAlarmByTime() = runBlocking {
        val alarm1 = AlarmItem(
            selectedTime = "08:00",
            alarmName = "Morning Alarm",
            lon = 33.1,
            lat = 31.1,
            selectedDate = "05/04/2025"
        )
        val alarm2 = AlarmItem(
            selectedTime = "09:00",
            alarmName = "Another Alarm",
            lon = 33.1,
            lat = 31.1,
            selectedDate = "06/04/2025"
        )

        dao.insertAlarm(alarm1)
        dao.insertAlarm(alarm2)

        val deleteCount = dao.deleteAlarmTime("08:00")

        val allAlarms = dao.getAllAlarms().first()
        assertEquals(1, allAlarms.size)
        assertEquals("09:00", allAlarms[0].selectedTime)
        assertEquals(1, deleteCount)
    }

}