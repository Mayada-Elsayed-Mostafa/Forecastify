package com.example.forecastify.favoriteLocations

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecastify.data.models.FavoriteLocation
import com.example.forecastify.data.repository.RepositoryImp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FavLocationsViewModel(private val repository: RepositoryImp) : ViewModel() {

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

    fun deleteLocation(id: Int) {
        viewModelScope.launch {
            repository.deleteLocation(id)
        }
    }

    fun getAddress(context: Context, latLng: LatLng, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                val address = if (addresses != null && addresses.isNotEmpty()) {
                    val firstAddress = addresses.first()

                    firstAddress.locality
                        ?: firstAddress.subAdminArea
                        ?: firstAddress.adminArea
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

}

class FavLocationsFactory(private val repository: RepositoryImp) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavLocationsViewModel(repository) as T
    }
}
