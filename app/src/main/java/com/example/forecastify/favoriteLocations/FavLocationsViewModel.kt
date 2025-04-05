package com.example.forecastify.favoriteLocations

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.repository.RepositoryImp
import com.example.forecastify.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FavLocationsViewModel(
    private val repository: RepositoryImp,
    private val settingsViewModel: SettingsViewModel,
) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()

    private val _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast: StateFlow<ForecastResponse?> = _forecast.asStateFlow()

    private val mutableMessage: MutableLiveData<String> = MutableLiveData()

    fun getFavoriteLocations() {
        viewModelScope.launch {
            repository.getFavoriteLocations()
        }
    }

    val favoriteLocations = repository.getFavoriteLocations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addLocation(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch {
            repository.addLocation(favoriteLocation)
        }
    }

    fun deleteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }

    fun getAddress(context: Context, latLng: LatLng, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                val address = if (!addresses.isNullOrEmpty()) {
                    val firstAddress = addresses.first()

                    firstAddress.locality ?: firstAddress.subAdminArea ?: firstAddress.adminArea
                    ?: "Unknown City"
                } else {
                    "Unknown Location"
                }

                withContext(Dispatchers.Main) {
                    onResult(address)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult("Unable to get address")
                }
            }
        }
    }

    fun getForecastOfFavLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getWeather(
                    true, lat = latitude, lon = longitude
                ).collect { result ->
                    val originalTemp = result.main.temp
                    val convertedTemp = settingsViewModel.convertTemperature(originalTemp)
                    val originalFeelsLike = result.main.feelsLike
                    val convertedFeelsLike = settingsViewModel.convertTemperature(originalFeelsLike)
                    val originalWindSpeed = result.wind.speed
                    val convertedWindSpeed = settingsViewModel.convertWindSpeed(
                        originalWindSpeed, settingsViewModel.getWindSpeedUnit()
                    )
                    val updatedWeather = result.copy(
                        main = result.main.copy(
                            temp = convertedTemp, feelsLike = convertedFeelsLike
                        ), wind = result.wind.copy(speed = convertedWindSpeed)
                    )
                    _weather.emit(updatedWeather)
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun getUpcomingForecastOfFavLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getUpcomingForecast(
                    isOnline = true, lat = latitude, lon = longitude
                ).collect { result ->
                    val updatedForecast = result.copy(list = result.list.map { forecastItem ->
                        val originalTemp = forecastItem.main.temp
                        val originalTempMax = forecastItem.main.temp_max
                        val originalTempMin = forecastItem.main.temp_min
                        val convertedTemp = settingsViewModel.convertTemperature(originalTemp)
                        val convertedTempMax = settingsViewModel.convertTemperature(originalTempMax)
                        val convertedTempMin = settingsViewModel.convertTemperature(originalTempMin)
                        forecastItem.copy(
                            main = forecastItem.main.copy(
                                temp = String.format("%.2f", convertedTemp).toDouble(),
                                temp_max = String.format("%.2f", convertedTempMax).toDouble(),
                                temp_min = String.format("%.2f", convertedTempMin).toDouble()
                            ),
                        )
                    })
                    _forecast.emit(updatedForecast)
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }
}

class FavLocationsFactory(
    private val repository: RepositoryImp,
    private val settingsViewModel: SettingsViewModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavLocationsViewModel(repository, settingsViewModel) as T
    }
}
