package com.example.forecastify.data.repository

import com.example.forecastify.data.local.LocalDataSourceImp
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.remote.RemoteDataSourceImp
import kotlinx.coroutines.flow.Flow

class RepositoryImp private constructor(
    private val remoteDataSource: RemoteDataSourceImp,
    private val localDataSource: LocalDataSourceImp,
) : Repository {

    override suspend fun getWeather(isOnline: Boolean, lat: Double, lon: Double): WeatherResponse? {
        return remoteDataSource.getCurrentWeather(lat, lon)
    }

    override suspend fun getUpcomingForecast(lat: Double, lon: Double): ForecastResponse? {
        return remoteDataSource.getForecast(lat, lon)
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
