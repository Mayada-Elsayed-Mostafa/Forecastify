package com.example.forecastify.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.data.repository.WeatherRepositoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepositoryImp) : ViewModel() {

    var lat = 30.0444
    var log = 31.2357

    private val mutableWeather: MutableLiveData<WeatherResponse> = MutableLiveData()
    val weather: LiveData<WeatherResponse> = mutableWeather

    private val mutableMessage: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = mutableMessage

    fun getWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getWeather(true, lat = lat, lon = log)
                if (result != null) {
                    var res: WeatherResponse = result
                    mutableWeather.postValue(res)
                } else {
                    mutableMessage.postValue("Please try again later...")
                }
            } catch (ex: Exception) {
                mutableMessage.postValue("An error occurred ${ex.message}")
            }
        }
    }

}

class HomeFactory(private val repository: WeatherRepositoryImp) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }

}