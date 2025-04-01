package com.example.forecastify.data.repository

import com.example.forecastify.data.local.LocalDataSourceImp
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.remote.RemoteDataSourceImp
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImp private constructor(
    private val remoteDataSource: RemoteDataSourceImp,
    private val localDataSource: LocalDataSourceImp,
) : WeatherRepository {

    override suspend fun getWeather(isOnline: Boolean, lat: Double, lon: Double): WeatherResponse? {
        return if (isOnline) {
            remoteDataSource.getCurrentWeather(lat, lon)
        } else {
            TODO("Not yet implemented")
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double): ForecastResponse? {
        return remoteDataSource.getForecastOfDay(lat, lon)
    }

    override suspend fun getUpcomingForecast(lat: Double, lon: Double): ForecastResponse? {
        return remoteDataSource.getForecast(lat, lon)
    }

    suspend fun getAllLocations(): Flow<List<FavoriteLocation>> {
        return localDataSource.getAllLocations()
    }

    suspend fun addLocation(location: FavoriteLocation) {
        localDataSource.insertLocation(location)
    }

    suspend fun deleteLocation(id: Int) {
        localDataSource.deleteLocation(id)
    }


    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImp? = null
        fun getInstance(
            remoteDataSource: RemoteDataSourceImp,
            localDataSource: LocalDataSourceImp,
        ): WeatherRepositoryImp {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepositoryImp(remoteDataSource, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
}
