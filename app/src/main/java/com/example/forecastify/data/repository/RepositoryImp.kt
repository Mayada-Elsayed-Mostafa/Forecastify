package com.example.forecastify.data.repository

import com.example.forecastify.data.local.LocalDataSourceImp
import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.remote.RemoteDataSourceImp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class RepositoryImp private constructor(
    private val remoteDataSource: RemoteDataSourceImp,
    private val localDataSource: LocalDataSourceImp,
) : Repository {

    override suspend fun getWeather(
        isOnline: Boolean,
        lat: Double,
        lon: Double,
    ): Flow<WeatherResponse> {
        return if (isOnline) {
            val result = remoteDataSource.getCurrentWeather(lat, lon)
            if (result != null) {
                localDataSource.insertWeather(result)
                flowOf(result)
            } else {
                emptyFlow()
            }
        } else {
            localDataSource.getWeatherFromRoom()
        }
    }

    override suspend fun getUpcomingForecast(
        isOnline: Boolean,
        lat: Double,
        lon: Double,
    ): Flow<ForecastResponse> {
        return if (isOnline) {
            val result = remoteDataSource.getForecast(lat, lon)
            if (result != null) {
                localDataSource.insertForecast(result)
                flowOf(result)
            } else {
                emptyFlow()
            }
        } else {
            localDataSource.getForecastFromRoom()
        }
    }

    override fun getFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return localDataSource.getAllLocations()
    }

    override suspend fun addLocation(favoriteLocation: FavoriteLocation) {
        localDataSource.insertLocation(favoriteLocation)
    }

    override suspend fun deleteLocation(id: Int) {
        localDataSource.deleteLocation(id)
    }

    override fun getAllAlarms(): Flow<List<AlarmItem>> {
        return localDataSource.getAllAlarms()
    }

    override suspend fun addAlarm(alarmItem: AlarmItem) {
        localDataSource.insertAlarm(alarmItem)
    }

    override suspend fun removeAlarm(alarmItem: AlarmItem) {
        localDataSource.deleteAlarm(alarmItem)
    }

    override suspend fun deleteAlarmTime(time: String) {
        localDataSource.deleteAlarmTime(time)
    }

    companion object {
        @Volatile
        private var INSTANCE: RepositoryImp? = null
        fun getInstance(
            remoteDataSource: RemoteDataSourceImp,
            localDataSource: LocalDataSourceImp,
        ): RepositoryImp {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryImp(remoteDataSource, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
}
