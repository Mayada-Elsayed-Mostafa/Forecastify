package com.example.forecastify

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.forecastify.alerts.AlertsFactory
import com.example.forecastify.alerts.AlertsScreen
import com.example.forecastify.alerts.AlertsViewModel
import com.example.forecastify.data.local.LocalDataSourceImp
import com.example.forecastify.data.local.SettingsDataStore
import com.example.forecastify.data.local.WeatherDatabase
import com.example.forecastify.data.remote.RemoteDataSourceImp
import com.example.forecastify.data.remote.Retrofit
import com.example.forecastify.data.repository.WeatherRepositoryImp
import com.example.forecastify.favoriteLocations.FavLocationsFactory
import com.example.forecastify.favoriteLocations.FavLocationsViewModel
import com.example.forecastify.favoriteLocations.FavoriteLocationsScreen
import com.example.forecastify.home.HomeFactory
import com.example.forecastify.home.HomeScreen
import com.example.forecastify.home.HomeViewModel
import com.example.forecastify.settings.SettingsFactory
import com.example.forecastify.settings.SettingsScreen
import com.example.forecastify.settings.SettingsViewModel
import com.example.forecastify.ui.theme.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

const val REQUEST_LOCATION_CODE = 2002

class MainActivity : ComponentActivity() {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    var locationState: MutableState<Location> = mutableStateOf(Location(""))
    var addressText: MutableState<String> = mutableStateOf("")

    override fun onStart() {
        super.onStart()

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFreshLocation()
        setContent {
            AppTheme {
                AppScreen(locationState.value)
            }
        }
    }

    //1
    private fun checkPermissions(): Boolean {
        var result = false
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            result = true
        }
        return result
    }

    //2
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //3
    private fun getFreshLocation() {

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), com.example.forecastify.home.REQUEST_LOCATION_CODE
            )

        }
        mFusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    val location = p0.lastLocation ?: return

                    locationState.value = location
                    val geocoder = Geocoder(this@MainActivity)
                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    addressText.value =
                        addresses?.get(0)?.getAddressLine(0) ?: "Address not found"
                }
            },
            Looper.myLooper()
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == com.example.forecastify.home.REQUEST_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }
        }
    }

    //4
    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

}

@Composable
fun AppScreen(locationState: Location) {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController) }, modifier = Modifier.fillMaxSize()) { it ->
        BottomNavGraph(navController, locationState)
    }
}

@Composable
fun BottomBar(navHostController: NavHostController) {
    val listOfScreens = arrayOf(
        BottomBarRoutes.HomeScreenRoute,
        BottomBarRoutes.FavoritesScreenRoute,
        BottomBarRoutes.AlertsScreenRoute,
        BottomBarRoutes.SettingsScreenRoute
    )
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    NavigationBar {
        listOfScreens.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(item.icon), contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination == item.route,
                onClick = {
                    navHostController.navigate(item.route) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavGraph(navHostController: NavHostController, locationState: Location) {
    NavHost(
        navController = navHostController,
        startDestination = BottomBarRoutes.HomeScreenRoute.route
    ) {
        composable(BottomBarRoutes.HomeScreenRoute.route) { backStackEntry ->
            val parentEntry = navHostController.currentBackStackEntry
            val context = LocalContext.current
            val viewModel = ViewModelProvider(
                parentEntry!!,
                HomeFactory(
                    WeatherRepositoryImp.getInstance(
                        RemoteDataSourceImp(Retrofit.apiService),
                        LocalDataSourceImp(WeatherDatabase.getInstance(context).getWeatherDao())
                    )
                )
            ).get(HomeViewModel::class.java)
            HomeScreen(navHostController, viewModel, locationState)
        }

        composable(BottomBarRoutes.FavoritesScreenRoute.route) { backStackEntry ->
            val parentEntry = navHostController.currentBackStackEntry
            val context = LocalContext.current
            val viewModel = ViewModelProvider(
                parentEntry!!,
                FavLocationsFactory(
                    WeatherRepositoryImp.getInstance(
                        RemoteDataSourceImp(Retrofit.apiService),
                        LocalDataSourceImp(WeatherDatabase.getInstance(context).getWeatherDao())
                    )
                )
            ).get(FavLocationsViewModel::class.java)
            FavoriteLocationsScreen(navHostController, viewModel)
        }

        composable(BottomBarRoutes.AlertsScreenRoute.route) { backStackEntry ->
            val parentEntry = navHostController.currentBackStackEntry
            val context = LocalContext.current
            val viewModel = ViewModelProvider(
                parentEntry!!,
                AlertsFactory(
                    WeatherRepositoryImp.getInstance(
                        RemoteDataSourceImp(Retrofit.apiService),
                        LocalDataSourceImp(WeatherDatabase.getInstance(context).getWeatherDao())
                    )
                )
            ).get(AlertsViewModel::class.java)
            AlertsScreen(navHostController, viewModel)
        }

        composable(BottomBarRoutes.SettingsScreenRoute.route) { backStackEntry ->
            val parentEntry = navHostController.currentBackStackEntry
            val context = LocalContext.current
            val settingsDataStore = SettingsDataStore(context)
            val viewModel = ViewModelProvider(
                parentEntry!!,
                SettingsFactory(settingsDataStore)
            ).get(SettingsViewModel::class.java)
            SettingsScreen(navHostController, viewModel)
        }
    }
}