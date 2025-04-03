package com.example.forecastify.alerts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.forecastify.data.local.AppDatabase
import com.example.forecastify.data.local.LocalDataSourceImp
import com.example.forecastify.data.remote.RemoteDataSourceImp
import com.example.forecastify.data.remote.Retrofit
import com.example.forecastify.data.repository.RepositoryImp
import com.example.forecastify.utils.MyNotificationChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class AlertWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val selectedTime = inputData.getString("selectedTime") ?: "Unknown time"
        val lat = inputData.getDouble("lat", 0.0)
        val lon = inputData.getDouble("lon", 0.0)

        val repo = RepositoryImp.getInstance(
            RemoteDataSourceImp(Retrofit.apiService), LocalDataSourceImp(
                AppDatabase.getInstance(applicationContext).getFavoriteDao(),
                AppDatabase.getInstance(applicationContext).getWeatherDao(),
                AppDatabase.getInstance(applicationContext).getForecastDao(),
                AppDatabase.getInstance(applicationContext).getAlarmDao(),
            )
        )

        return withContext(Dispatchers.IO) {
            try {
                val weatherData = repo.getWeather(true, lat, lon).firstOrNull()
                val weatherDescription =
                    weatherData?.weather?.firstOrNull()?.description ?: "Unknown"
                val city = weatherData?.name ?: "Unknown"

                sendNotification(city, weatherDescription)
                repo.deleteAlarmTime(selectedTime)
                Result.success()

            } catch (ex: Exception) {
                sendNotification("Unknown", "No Internet")
                repo.deleteAlarmTime(selectedTime)
                Result.failure()
            }
        }
    }

    private fun sendNotification(city: String, message: String) {
        MyNotificationChannel.createNotificationChannel(applicationContext, city, message)
    }
}
