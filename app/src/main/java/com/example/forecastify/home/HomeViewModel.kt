package com.example.forecastify.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.repository.WeatherRepositoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val REQUEST_LOCATION_CODE = 2002

class HomeViewModel(private val repository: WeatherRepositoryImp) : ViewModel() {

    private val mutableWeather: MutableLiveData<WeatherResponse> = MutableLiveData()
    val weather: LiveData<WeatherResponse> = mutableWeather

    private val mutableForecast: MutableLiveData<ForecastResponse> = MutableLiveData()
    val forecast: LiveData<ForecastResponse> = mutableForecast

    private val mutableMessage: MutableLiveData<String> = MutableLiveData()

    fun getWeather(locationState: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getWeather(
                    true,
                    lat = locationState.latitude,
                    lon = locationState.longitude
                )
                if (result != null) {
                    val res: WeatherResponse = result
                    mutableWeather.postValue(res)
                } else {
                    mutableMessage.postValue("Please try again later...")
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }

    fun getForecast(locationState: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getForecast(
                    lat = locationState.latitude,
                    lon = locationState.longitude
                )
                if (result != null) {
                    val res: ForecastResponse = result
                    mutableForecast.postValue(res)
                } else {
                    mutableMessage.postValue("Please try again later...")
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class HomeFactory(private val repository: WeatherRepositoryImp) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }

}