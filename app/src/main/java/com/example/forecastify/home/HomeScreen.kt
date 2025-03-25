package com.example.forecastify.home

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forecastify.R
import com.example.forecastify.data.models.WeatherResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController, viewModel: HomeViewModel, location: Location) {

    viewModel.getWeather(location)
    val currentWeather = viewModel.weather.observeAsState()

    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Today", "Tomorrow", "5 days")

    Column {

        CurrentWeatherComponent(currentWeather.value)

        PrimaryTabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (state == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        DailyDetails(currentWeather.value)
        HourlyDetails(currentWeather.value)
    }
}

@Composable
fun CurrentWeatherComponent(weather: WeatherResponse?) {
    val currentDateTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val formattedDate = currentDateTime.format(dateFormatter)
    val formattedTime = currentDateTime.format(timeFormatter)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.weather_background),
            contentDescription = "Weather Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Card(
            modifier = Modifier
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Row {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row {
                        Text(
                            text = weather?.name ?: "Unknown Location",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(60.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row {
                            Text(
                                text = "${weather?.main?.temp?.toInt() ?: "--"}",
                                fontSize = 48.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "°K",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Text(
                            text = "${weather?.main?.tempMax?.toInt() ?: "--"}/${weather?.main?.tempMin?.toInt() ?: "--"} Feels like ${weather?.main?.feelsLike?.toInt() ?: "--"}°K",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                    Row {
                        Text(
                            text = formattedDate,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formattedTime,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(start = 72.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cloudy_icon),
                        contentDescription = "Weather Icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        text = weather?.weather?.getOrNull(0)?.main ?: "--",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = weather?.weather?.getOrNull(0)?.description?.replaceFirstChar { it.uppercase() }
                            ?: "--",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyDetails(weather: WeatherResponse?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wind_speed),
                        contentDescription = "Wind Speed Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Wind Speed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${weather?.wind?.speed} m/s", fontSize = 14.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pressure),
                        contentDescription = "Pressure Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Pressure", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${weather?.main?.pressure} hPa", fontSize = 14.sp)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rain_chance),
                        contentDescription = "Rain Chance Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Rain Chance", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${weather?.clouds?.all}%", fontSize = 14.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.uv_index),
                        contentDescription = "Humidity Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Humidity", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${weather?.main?.humidity}%", fontSize = 14.sp)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val sunriseUtc = weather?.sys?.sunrise ?: 0
            val sunsetUtc = weather?.sys?.sunset ?: 0
            val timezoneOffset = weather?.timezone ?: 0

            val sunriseLocal = Instant.ofEpochSecond(sunriseUtc + timezoneOffset)
                .atZone(ZoneId.of("UTC"))
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"))

            val sunsetLocal = Instant.ofEpochSecond(sunsetUtc + timezoneOffset)
                .atZone(ZoneId.of("UTC"))
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sunrise),
                        contentDescription = "Sun Rise Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Sunrise", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = sunriseLocal, fontSize = 14.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sunset),
                        contentDescription = "Sunset Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Sunset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = sunsetLocal, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HourlyDetails(weather: WeatherResponse?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Hourly Forecast",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(5) { index ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(text = "${index * 3}h", fontSize = 14.sp)
                        Image(
                            painter = painterResource(id = R.drawable.cloudy_icon),
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "${weather?.main?.temp?.toInt() ?: "--"}°K", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}