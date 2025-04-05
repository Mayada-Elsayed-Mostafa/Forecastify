package com.example.forecastify.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.forecastify.data.models.AlarmItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceImpTest {

    lateinit var favoriteDao: FavoriteDao
    lateinit var weatherDao: WeatherDao
    lateinit var forecastDao: ForecastDao
    lateinit var alarmDao: AlarmDao
    lateinit var database: AppDatabase
    lateinit var localDataSource: LocalDataSourceImp

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        favoriteDao = database.getFavoriteDao()
        weatherDao = database.getWeatherDao()
        forecastDao = database.getForecastDao()
        alarmDao = database.getAlarmDao()
        localDataSource = LocalDataSourceImp(favoriteDao, weatherDao, forecastDao, alarmDao)
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun insertAlarm_test() = runTest {
        val alarmItem = AlarmItem(
            alarmName = "Morning Alarm",
            selectedDate = "2025-04-05",
            selectedTime = "07:00",
            lat = 30.0444,  // قيمة افتراضية لخط العرض
            lon = 31.2357   // قيمة افتراضية لخط الطول
        )

        // إدخال التنبيه
        localDataSource.insertAlarm(alarmItem)

        // التحقق من أن السجل تم إدخاله بشكل صحيح
        val alarms = localDataSource.getAllAlarms().first()  // استخدم Flow لجلب البيانات بشكل صحيح
        assertEquals(1, alarms.size)  // تأكد من أنه تم إدخال سجل واحد
        val insertedAlarm = alarms.first()  // الحصول على أول عنصر في القائمة

        // التحقق من أن القيم المدخلة تطابق المدخلات
        assertEquals("Morning Alarm", insertedAlarm.alarmName)
        assertEquals("2025-04-05", insertedAlarm.selectedDate)
        assertEquals("07:00", insertedAlarm.selectedTime)
        assertEquals(30.0444, insertedAlarm.lat, 0.0001)  // دقة العائمة
        assertEquals(31.2357, insertedAlarm.lon, 0.0001)  // دقة العائمة
    }


}