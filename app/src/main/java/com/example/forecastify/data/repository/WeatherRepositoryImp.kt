package com.example.forecastify.data.repository

import com.example.forecastify.data.local.LocalDataSourceImp
import com.example.forecastify.data.remote.RemoteDataSourceImp

class WeatherRepositoryImp(
    private val remoteDataSource: RemoteDataSourceImp,
    private val localDataSource: LocalDataSourceImp,
) : WeatherRepository {


}
