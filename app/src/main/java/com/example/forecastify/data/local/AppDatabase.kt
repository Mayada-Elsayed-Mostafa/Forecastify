package com.example.forecastify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse

@Database(
    entities = [AlarmItem::class, FavoriteLocation::class, WeatherResponse::class, ForecastResponse::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(WeatherTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFavoriteDao(): FavoriteDao
    abstract fun getAlarmDao(): AlarmDao
    abstract fun getWeatherDao(): WeatherDao
    abstract fun getForecastDao(): ForecastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "All_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
