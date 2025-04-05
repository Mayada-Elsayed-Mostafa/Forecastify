package com.example.forecastify.data.remote

import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeRemoteDataSource: RemoteDataSource {

    //Create pojo

//    override suspend fun getCurrentWeather(lat: Double, lon: Double,unit:String): Flow<CurrentWeather> {
//        return flowOf(fakeCurrentWeather)
//        }
    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        TODO("Not yet implemented")
    }
}