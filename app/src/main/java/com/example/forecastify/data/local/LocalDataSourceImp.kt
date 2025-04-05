package com.example.forecastify.data.local

import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow


class LocalDataSourceImp(
    private val favoriteDao: FavoriteDao,
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao,
    private val alarmDao: AlarmDao,
) : LocalDataSource {

    override fun getWeatherFromRoom(): Flow<WeatherResponse> {
        return weatherDao.getWeather()
    }

    override fun getForecastFromRoom(): Flow<ForecastResponse> {
        return forecastDao.getForecast()
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponse) {
        weatherDao.insertWeather(weatherResponse)
    }

    override suspend fun insertForecast(forecastResponse: ForecastResponse) {
        forecastDao.insertForecast(forecastResponse)
    }

    override fun getAllLocations(): Flow<List<FavoriteLocation>> {
        return favoriteDao.getAllLocations()
    }

    override suspend fun insertLocation(location: FavoriteLocation) {
        return favoriteDao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FavoriteLocation) {
        favoriteDao.deleteLocation(location)
    }

    override fun getAllAlarms(): Flow<List<AlarmItem>> {
        return alarmDao.getAllAlarms()
    }

    override suspend fun insertAlarm(alertItem: AlarmItem) {
        alarmDao.insertAlarm(alertItem)
    }

    override suspend fun deleteAlarm(alarmItem: AlarmItem) {
        alarmDao.deleteAlarm(alarmItem)
    }

    override suspend fun deleteAlarmTime(time: String) {
        alarmDao.deleteAlarmTime(time)
    }

}
