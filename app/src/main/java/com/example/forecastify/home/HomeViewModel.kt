package com.example.forecastify.home

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.repository.RepositoryImp
import com.example.forecastify.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val REQUEST_LOCATION_CODE = 2002

class HomeViewModel(
    private val repository: RepositoryImp,
    private val settingsViewModel: SettingsViewModel,
) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()

    private val _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast: StateFlow<ForecastResponse?> = _forecast.asStateFlow()

    private val mutableMessage: MutableLiveData<String> = MutableLiveData()

    fun getWeather(locationState: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getWeather(
                    true, lat = locationState.latitude, lon = locationState.longitude
                ).collect { result ->
                    if (result != null) {
                        val originalTemp = result.main.temp
                        val convertedTemp = settingsViewModel.convertTemperature(originalTemp)
                        val originalFeelsLike = result.main.feelsLike
                        val convertedFeelsLike =
                            settingsViewModel.convertTemperature(originalFeelsLike)
                        val originalWindSpeed = result.wind.speed
                        val convertedWindSpeed = settingsViewModel.convertWindSpeed(
                            originalWindSpeed, settingsViewModel.getWindSpeedUnit()
                        )
                        val updatedWeather = result.copy(
                            main = result.main.copy(
                                temp = convertedTemp, feelsLike = convertedFeelsLike
                            ), wind = result.wind.copy(speed = convertedWindSpeed)
                        )
                        _weather.value = updatedWeather
                    } else {
                        mutableMessage.postValue("Please try again later...")
                    }
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun getUpcomingForecast(locationState: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getUpcomingForecast(
                    true, lat = locationState.latitude, lon = locationState.longitude
                ).collect { result ->
                    if (result != null) {
                        val updatedForecast = result.copy(list = result.list.map { forecastItem ->
                            val originalTemp = forecastItem.main.temp
                            val originalTempMax = forecastItem.main.temp_max
                            val originalTempMin = forecastItem.main.temp_min
                            val convertedTemp = settingsViewModel.convertTemperature(originalTemp)
                            val convertedTempMax =
                                settingsViewModel.convertTemperature(originalTempMax)
                            val convertedTempMin =
                                settingsViewModel.convertTemperature(originalTempMin)
                            forecastItem.copy(
                                main = forecastItem.main.copy(
                                    temp = String.format("%.2f", convertedTemp).toDouble(),
                                    temp_max = String.format("%.2f", convertedTempMax).toDouble(),
                                    temp_min = String.format("%.2f", convertedTempMin).toDouble()
                                ),
                            )
                        })
                        _forecast.value = updatedForecast
                    } else {
                        mutableMessage.postValue("Please try again later...")
                    }
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class HomeFactory(
    private val repository: RepositoryImp,
    private val settingsViewModel: SettingsViewModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, settingsViewModel) as T
    }
}
