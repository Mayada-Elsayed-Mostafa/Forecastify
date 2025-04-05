package com.example.forecastify.favoriteLocations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.forecastify.R
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.home.getTodayForecast
import com.example.forecastify.ui.theme.inversePrimaryLight
import com.example.forecastify.ui.theme.onPrimaryLight
import com.example.forecastify.ui.theme.primaryContainerLight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun FavoriteLocationDetailsScreen(viewModel: FavLocationsViewModel, lat: Double, lon: Double) {

    val currentWeather by viewModel.weather.collectAsState()
    val currentForecast by viewModel.forecast.collectAsState()


    LaunchedEffect(lat, lon) {
        viewModel.getForecastOfFavLocation(lat, lon)
        viewModel.getUpcomingForecastOfFavLocation(lat, lon)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = inversePrimaryLight), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "${currentWeather?.name}", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "${currentWeather?.sys?.country}", style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(
                        "https://openweathermap.org/img/wn/${
                            currentWeather?.weather?.getOrNull(0)?.icon
                        }.png"
                    ), contentDescription = "Weather Icon", modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${currentWeather?.weather?.getOrNull(0)?.description}")
                    Row {
                        Text(
                            text = "${currentWeather?.main?.temp?.toInt()}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = stringResource(R.string.k),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(stringResource(R.string.feels_like))
                Spacer(modifier = Modifier.width(8.dp))
                Row {
                    Text(text = "${currentWeather?.main?.feelsLike?.toInt()}")
                    Text(text = stringResource(R.string.k))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(stringResource(R.string.humidity))
                Spacer(modifier = Modifier.width(8.dp))
                Text("${currentWeather?.main?.humidity}%")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(stringResource(R.string.wind_speed))
                Spacer(modifier = Modifier.width(8.dp))
                Text("${currentWeather?.wind?.speed} m/s")
            }
            Text("Next hours")
            HourlyDetails(getTodayForecast(currentForecast))
        }

    }
}


@Composable
fun HourlyDetails(forecastResponse: ForecastResponse?) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val forecastList = forecastResponse?.list ?: emptyList()
        items(forecastList.take(8)) { weatherItem ->
            val time =
                Instant.ofEpochSecond(weatherItem.dt).atZone(ZoneId.systemDefault()).toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"))

            Card(
                modifier = Modifier
                    .width(75.dp)
                    .height(150.dp)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = time,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = onPrimaryLight
                    )
                    Image(
                        painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${weatherItem.weather.firstOrNull()?.icon}.png"),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Row {
                        Text(
                            text = "${weatherItem.main.temp.toInt()}",
                            fontSize = 14.sp,
                            color = onPrimaryLight
                        )
                        Text(
                            text = stringResource(R.string.k),
                            fontSize = 14.sp,
                            color = onPrimaryLight
                        )
                    }
                }
            }
        }
    }
}
