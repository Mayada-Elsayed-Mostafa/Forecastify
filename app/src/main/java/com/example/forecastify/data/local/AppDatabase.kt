package com.example.forecastify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forecastify.data.models.WeatherResponse

@Database(
    entities = [WeatherResponse::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WeatherTypeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}