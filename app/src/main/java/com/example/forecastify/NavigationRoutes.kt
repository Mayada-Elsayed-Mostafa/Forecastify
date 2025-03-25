package com.example.forecastify

import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarRoutes(
    val route: String,
    val title: String,
    val icon: Int,
) {
    @Serializable
    object HomeScreenRoute : BottomBarRoutes("home", "Home", R.drawable.baseline_home_24)

    @Serializable
    object FavoritesScreenRoute :
        BottomBarRoutes("favorites", "Favorites", R.drawable.baseline_add_location_alt_24)

    @Serializable
    object AlertsScreenRoute :
        BottomBarRoutes("alerts", "Alerts", R.drawable.baseline_add_alert_24)

    @Serializable
    object SettingsScreenRoute :
        BottomBarRoutes("settings", "Settings", R.drawable.baseline_app_settings_alt_24)
}
