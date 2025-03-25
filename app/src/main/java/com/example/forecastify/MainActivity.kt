package com.example.forecastify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppScreen()
            }
        }
    }
}

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController) }, modifier = Modifier.fillMaxSize()) { it ->
        Log.i("TAG", "AppScreen: $it")
        BottomNavGraph(navController)
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
fun BottomNavGraph(navHostController: NavHostController) {
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
            HomeScreen(navHostController, viewModel)
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