package com.example.forecastify.favoriteLocations

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.forecastify.R
import com.example.forecastify.data.models.FavoriteLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteLocationsScreen(
    navHostController: NavHostController,
    viewModel: FavLocationsViewModel,
    context: Context,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

    val favoriteLocations by viewModel.favoriteLocations.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Filled.LocationOn, "Add Favorite Location")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier.padding(bottom = 105.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteLocations.size) { index ->
                    val location = favoriteLocations[index]
                    FavLocationItem(location) {
                        navHostController.navigate("locationDetails/${location.lat}/${location.lon}")
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.select_favorite_location),
                    style = MaterialTheme.typography.headlineSmall
                )

                Button(
                    onClick = {
                        selectedPosition?.let { position ->
                            viewModel.getAddress(context, position) { address ->
                                val newLocation = FavoriteLocation(
                                    address = address,
                                    lat = position.latitude,
                                    lon = position.longitude
                                )

                                viewModel.addLocation(newLocation)
                                showBottomSheet = false
                            }
                        }
                    },
                    enabled = selectedPosition != null,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Text(stringResource(R.string.save_location))
                }

                MapScreen { newPosition ->
                    selectedPosition = newPosition
                }
            }
        }
    }
}


@Composable
fun FavLocationItem(location: FavoriteLocation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = location.address,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate to details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun MapScreen(onLocationSelected: (LatLng) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 5f)
    }
    var selectedPosition by remember { mutableStateOf(LatLng(30.0444, 31.2357)) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedPosition ->
                selectedPosition = clickedPosition
                onLocationSelected(clickedPosition)
            }
        ) {
            Marker(
                state = MarkerState(position = selectedPosition),
                title = stringResource(R.string.selected_location),
                snippet = "Lat: ${selectedPosition.latitude}, Lng: ${selectedPosition.longitude}"
            )
        }
    }
}
